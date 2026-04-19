package com.authService.controller;

import com.authService.dto.ApiResponse;
import com.authService.dto.ForgetPasswordRequestDto;
import com.authService.dto.OtpRequestDto;
import com.authService.dto.ResetPasswordRequestDto;
import com.authService.services.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService service;

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgot(@RequestBody ForgetPasswordRequestDto req) {
        return ResponseEntity.ok(service.forgotPassword(req.getEmail()));
    }

    @PostMapping("/generate-otp")
    public ResponseEntity<ApiResponse> generateOtp(@RequestParam String token) {
        return ResponseEntity.ok(service.generateOtp(token));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@RequestBody OtpRequestDto req) {
        return ResponseEntity.ok(service.verifyOtp(req));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody ResetPasswordRequestDto req) {
        return ResponseEntity.ok(service.resetPassword(req));
    }
}