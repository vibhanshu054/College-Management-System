package com.authService.services;

import com.authService.dto.ApiResponse;
import com.authService.dto.OtpRequestDto;
import com.authService.dto.ResetPasswordRequestDto;
public interface PasswordService {

    ApiResponse forgotPassword(String email);

    ApiResponse generateOtp(String token);

    ApiResponse verifyOtp(OtpRequestDto request);

    ApiResponse resetPassword(ResetPasswordRequestDto request);
}