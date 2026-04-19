package com.authService.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class AuthResponse {

    private String token;
    private String username;
    private Long id;
    private String universityId;
    private LocalDateTime timeStamp;
    private LocalDateTime expiryTime;
    private String type = "Bearer";
    private Long expiresIn;
    private String email;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;
}