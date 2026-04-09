package com.authService.services.impl;

import com.authService.dto.ApiResponse;
import com.authService.dto.AuthResponse;
import com.authService.dto.UserDto;
import com.authService.entity.BlacklistedToken;
import com.authService.exception.InvalidCredentialsException;
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

    //  LOGIN
    public AuthResponse login(String username, String password) {

        log.info("Authenticating user {}", username);

        // CALL User SERVICE
        UserDto user = restTemplate.getForObject(
                "http://User-Service/api/users/login?username={username}",
                UserDto.class,
                username
        );

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Invalid credentials for {}", username);
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String role = user.getRole() != null ? user.getRole() : "USER";
        String token = jwtUtil.generateToken(user.getUsername(), role);

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .timeStamp(LocalDateTime.now())
                .expiryTime(LocalDateTime.now().plusHours(1))
                .build();
    }

    //  VALIDATE TOKEN
    public boolean validateToken(String token) {

        // If token is blacklisted → reject
        log.info("Validating token");

        if (repository.existsByToken(token)) {
            log.warn("Token is blacklisted");
            return false;
        }

        try {
            jwtUtil.extractUsername(token); // validates token
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // LOGOUT
    public ApiResponse logout(String token, String username) {
        log.warn("BLACKLISTING TOKEN: {}", token);
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