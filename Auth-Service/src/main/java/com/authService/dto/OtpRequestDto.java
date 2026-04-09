package com.authService.dto;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OtpRequestDto {
    private String token;
    private int otp;
}