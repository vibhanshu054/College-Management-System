package com.authService.controller;

import com.authService.dto.ForgetPasswordRequestDto;
import com.authService.dto.OtpRequestDto;
import com.authService.dto.ResetPasswordRequestDto;
import com.authService.services.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService service;

    @PostMapping("/forgot-password")
    public String forgot(@RequestBody ForgetPasswordRequestDto req) {
        return service.forgotPassword(req.getEmail());
    }

    @PostMapping("/generate-otp")
    public String generateOtp(@RequestParam String token) {
        return service.generateOtp(token);
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestBody OtpRequestDto req) {
        return service.verifyOtp(req);
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordRequestDto req) {
        return service.resetPassword(req);
    }
}