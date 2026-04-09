package com.userService.exception;


import com.userService.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.IllegalArgumentException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleNotFound(UserNotFoundException ex) {

        log.error("Employee not found: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 404);
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    //IllegalArgument
    @ExceptionHandler(java.lang.IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgument(IllegalArgumentException ex) {

        log.error("Illegal Argument: {}", ex.getMessage());

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid data",
                ex.getMessage()
        );
    }

    // Invalid Credentials
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse> handleInvalidCredentials(InvalidCredentialsException ex) {

        log.warn("Invalid Credentials EXCEPTION: {}", ex.getMessage());

        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid Credentials Exception", ex.getMessage());
    }

    // SAME PASSWORD OLD==NEW
    @ExceptionHandler(SamePasswordException.class)
    public ResponseEntity<ApiResponse> handleSamePassword(SamePasswordException ex) {

        log.warn("SAME PASSWORD Old = New: {}", ex.getMessage());

        return buildResponse(HttpStatus.BAD_REQUEST, "Same Password", ex.getMessage());
    }

    //Email already exists
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse> handleDuplicateEmail(DuplicateEmailException ex) {

        log.warn("User with this Email already exists: {}", ex.getMessage());

        return buildResponse(HttpStatus.BAD_REQUEST, "User with this Email already exists", ex.getMessage());
    }

    //Username already exists
    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<ApiResponse> handleDuplicateUsername(DuplicateUsernameException ex) {

        log.warn("User with this Username already exists: {}", ex.getMessage());

        return buildResponse(HttpStatus.BAD_REQUEST, "User with this Username already exists", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {

        log.error("Exception: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 500);
        body.put("message", "Internal Server Error");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // COMMON BUILDER METHOD
    private ResponseEntity<ApiResponse> buildResponse(HttpStatus status, String message, String reason) {

        ApiResponse response = ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .message(message)
                .reason(reason)
                .build();

        return new ResponseEntity<>(response, status);
    }
}