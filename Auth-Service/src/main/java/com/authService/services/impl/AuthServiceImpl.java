package com.authService.services.impl;

import com.authService.dto.ApiResponse;
import com.authService.dto.AuthResponse;
import com.authService.dto.LoginRequest;
import com.authService.dto.UserDTO;
import com.authService.entity.BlacklistedToken;
import com.authService.exception.InvalidCredentialsException;
import com.authService.exception.InvalidTokenException;
import com.authService.exception.UserNotFoundException;
import com.authService.repository.BlacklistRepository;
import com.authService.services.AuthService;
import com.authService.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;
    private final BlacklistRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;

    @Override
    public AuthResponse login(String email, String password) {
        log.info("Authenticating user {}", email);

        // 1. Null / blank checks
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidCredentialsException("Email must not be null or blank");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new InvalidCredentialsException("Password must not be null or blank");
        }

        UserDTO user;
        try {
            user = restTemplate.postForObject(
                    "http://USER-SERVICE/api/users/verify",
                    new LoginRequest(email, password),
                    UserDTO.class
            );
        } catch (Exception ex) {
            log.error("Error while calling USER-SERVICE: {}", ex.getMessage(), ex);
            throw new InvalidCredentialsException("Unable to verify user credentials");
        }

        // 2. User null
        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + email);
        }

        // 3. Wrong password
//        if (user.getPassword() == null || !passwordEncoder.matches(password, user.getPassword())) {
//            log.warn("Invalid credentials for {}", email);
//            throw new InvalidCredentialsException("Invalid email or password");
//        }

        String role = user.getRole() != null ? user.getRole() : "USER";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusHours(1);

        String token = jwtUtil.generateToken(
                user.getEmail(),
                role,
                user.getDepartment() != null ? user.getDepartment() : "",
                user.getUniversityId() != null ? user.getUniversityId() : ""
        );

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .username(user.getUsername())
                .email(user.getEmail())
                .id(user.getId())
                .universityId(user.getUniversityId())
                .timeStamp(now)
                .issuedAt(now)
                .expiryTime(expiry)
                .expiresAt(expiry)
                .expiresIn(3600L)
                .build();
    }

    @Override
    public boolean validateToken(String token) {
        log.info("Validating token");

        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        if (repository.existsByToken(token)) {
            log.warn("Token is blacklisted");
            return false;
        }

        try {
            jwtUtil.extractUsername(token);
            return true;
        } catch (Exception e) {
            log.warn("Invalid token: {}", e.getMessage());
            return false;
        }
    }

    // LOGOUT
    @Override
    public ApiResponse logout(String token, String username) {
        log.warn("BLACKLISTING TOKEN: {}", token);

        if (token == null || token.trim().isEmpty()) {
            throw new InvalidTokenException("Token must not be null or blank");
        }

        if (username == null || username.trim().isEmpty()) {
            throw new InvalidCredentialsException("Username must not be null or blank");
        }

        if (repository.existsByToken(token)) {
            throw new InvalidTokenException("Token already blacklisted");
        }

        BlacklistedToken entity = BlacklistedToken.builder()
                .token(token)
                .expiryTime(LocalDateTime.now().plusHours(1))
                .build();

        repository.save(entity);

        return ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Logged out successfully")
                .username(username)
                .reason("Token invalidated")
                .expiryTime(entity.getExpiryTime())
                .build();
    }
}