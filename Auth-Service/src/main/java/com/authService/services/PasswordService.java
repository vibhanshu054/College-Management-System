package com.authService.services;

import com.authService.dto.OtpRequestDto;
import com.authService.dto.ResetPasswordRequestDto;

public interface PasswordService {
    String forgotPassword(String email);

    String generateOtp(String token);

    String verifyOtp(OtpRequestDto req);

    String resetPassword(ResetPasswordRequestDto req);
}
