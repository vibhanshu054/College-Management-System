package com.authService.client;

import com.authService.dto.LoginRequest;
import com.authService.dto.LoginResponse;
import com.authService.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for UserServiceClient
 */
@Component
@Slf4j
public class UserServiceClientFallback implements UserServiceClient {

    @Override
    public ResponseEntity<UserDTO> getUserByEmail(String email) {
        log.warn("User-Service is unavailable. Fallback: Unable to get user by email");
        return ResponseEntity.status(503).build();
    }

    @Override
    public ResponseEntity<UserDTO> verifyUserCredentials(LoginRequest loginRequest) {
        log.warn("User-Service is unavailable. Fallback: Unable to verify credentials");
        return ResponseEntity.status(503).build();
    }
}
