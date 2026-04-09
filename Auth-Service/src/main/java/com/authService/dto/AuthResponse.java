package com.authService.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class AuthResponse {

    private String token;
    private String username;
    private LocalDateTime timeStamp;
    private LocalDateTime expiryTime;
}