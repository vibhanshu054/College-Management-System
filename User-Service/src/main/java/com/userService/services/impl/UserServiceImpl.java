package com.userService.services.impl;

import com.userService.dto.UpdatePasswordDto;
import com.userService.dto.UserDto;
import com.userService.entity.UserEntity;
import com.userService.exception.*;
import com.userService.exception.IllegalArgumentException;
import com.userService.repository.UserRepository;
import com.userService.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {


    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto save(UserDto dto) {

        log.info("Saving User {}", dto.getUsername());

        // VALIDATION
        if (dto.getUsername() == null || dto.getUsername().isBlank() ||
                dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Username and password are required");
        }

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        validatePassword(dto.getPassword());

        // CHECK DUPLICATES
        if (repository.existsByUsername(dto.getUsername())) {
            throw new DuplicateUsernameException("Username already exists");
        }

        if (repository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException("Email already exists");
        }

        // CREATE ENTITY
        UserEntity emp = UserEntity.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole() != null ? dto.getRole() : "USER")
                .build();

        UserEntity saved = repository.save(emp);

        return new UserDto(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getPassword(),
                saved.getRole()
        );
    }


    // PASSWORD VALIDATION METHOD
    private void validatePassword(String password) {
        String regex = "^(?=(.*[a-z]){4,})(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$";
        if (!password.matches(regex)) {
            throw new IllegalArgumentException(
                    "Password must contain at least 1 uppercase, 4 lowercase, 1 number, and 1 special character"
            );
        }
    }

    // Get all User
    public List<UserEntity> getAllUser() {
        log.info("Fetching all User");
        return repository.findAll();
    }

    // Get User by ID
    public UserDto getUser(Long id) {
        log.info("Fetching User {}", id);
        UserEntity emp = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return new UserDto(emp.getId(), emp.getUsername(), emp.getEmail(), emp.getPassword(), emp.getRole());
    }

    // Find by username
    public UserDto findByUsername(String username) {
        log.info("Finding user {}", username);
        UserEntity emp = repository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return new UserDto(emp.getId(), emp.getUsername(), emp.getEmail(), emp.getPassword(), emp.getRole());
    }

    // Login
@Override
    public UserDto login(String username, String password) {
        log.info("Validating user {}", username);
        UserEntity emp = repository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, emp.getPassword())) {
            log.error("Invalid password for {}", username);
            throw new InvalidCredentialsException("Invalid credentials");
        }

        log.info("Login successful for {}", username);
        return new UserDto(emp.getId(), emp.getUsername(), emp.getEmail(), emp.getPassword(), emp.getRole());
    }

    @Override
    public String updatePassword(UpdatePasswordDto dto) {
        if (dto.getUsername() == null || dto.getNewPassword() == null) {
            throw new IllegalArgumentException("Username or password cannot be null");
        }

        UserEntity user = repository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Stored password is invalid");
        }

        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new SamePasswordException("New password cannot be same as old password");
        }

        validatePassword(dto.getNewPassword());

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        repository.save(user);

        return "Password updated successfully";
    }

    @Override
    public UserDto findByEmail(String email) {
        UserEntity emp = repository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return new UserDto(emp.getId(), emp.getUsername(), emp.getEmail(), emp.getPassword(), emp.getRole());
    }
}