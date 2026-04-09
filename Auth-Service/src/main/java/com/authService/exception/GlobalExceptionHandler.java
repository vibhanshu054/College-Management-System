package com.authService.exception;

import com.authService.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // GENERIC EXCEPTION
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception ex) {

        log.error("GLOBAL EXCEPTION: {}", ex.getMessage(), ex);

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong", ex.getMessage());
    }

    // PASSWORD MISMATCH
    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ApiResponse> handlePasswordMismatch(PasswordMismatchException ex) {

        log.warn("PASSWORD MISMATCH: {}", ex.getMessage());

        return buildResponse(HttpStatus.BAD_REQUEST, "Password mismatch", ex.getMessage());
    }

    // SAME PASSWORD OLD==NEW
    @ExceptionHandler(SamePasswordException.class)
    public ResponseEntity<ApiResponse> handleSamePassword(SamePasswordException ex) {

        log.warn("SAME PASSWORD Old = New: {}", ex.getMessage());

        return buildResponse(HttpStatus.BAD_REQUEST, "Same Password", ex.getMessage());
    }

    // OTP NOT FOUND
    @ExceptionHandler(OtpNotFoundException.class)
    public ResponseEntity<ApiResponse> handleOtpNotFound(OtpNotFoundException ex) {

        log.warn("OTP NOT FOUND EXCEPTION: {}", ex.getMessage());

        return buildResponse(HttpStatus.BAD_REQUEST, "OTP Not Found", ex.getMessage());
    }

    //INVALID OTP
    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ApiResponse> handleInvalidOtp(InvalidOtpException ex) {

        log.warn("Invalid OTP EXCEPTION: {}", ex.getMessage());

        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid OTP", ex.getMessage());
    }

    // INVALID TOKEN
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse> handleInvalidToken(InvalidTokenException ex) {

        log.warn("INVALID TOKEN: {}", ex.getMessage());

        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid token", ex.getMessage());
    }

    // OTP EXPIRED
    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<ApiResponse> handleOtpExpired(OtpExpiredException ex) {

        log.warn("OTP EXPIRED: {}", ex.getMessage());

        return buildResponse(HttpStatus.BAD_REQUEST, "OTP expired", ex.getMessage());
    }

    // OTP NOT VERIFIED
    @ExceptionHandler(OtpNotVerifiedException.class)
    public ResponseEntity<ApiResponse> handleOtpNotVerified(OtpNotVerifiedException ex) {

        log.warn("OTP NOT VERIFIED: {}", ex.getMessage());

        return buildResponse(HttpStatus.BAD_REQUEST, "OTP not verified", ex.getMessage());
    }

    // USER NOT FOUND
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotFound(UserNotFoundException ex) {

        log.warn("USER NOT FOUND EXCEPTION: {}", ex.getMessage());

        return buildResponse(HttpStatus.BAD_REQUEST, "User Not Found", ex.getMessage());
    }

    // Invalid Credentials
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse> handleInvalidCredentials(InvalidCredentialsException ex) {

        log.warn("Invalid Credentials EXCEPTION: {}", ex.getMessage());

        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid Credentials Exception", ex.getMessage());
    }

    // METHOD NOT ALLOWED
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse> handleMethodNotSupported(Exception ex) {

        log.warn("METHOD NOT ALLOWED");

        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "Invalid request method", "Use correct HTTP method");
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