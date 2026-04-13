package com.userService.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class ApiResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String username;
    private String reason;
    private String path;
    private LocalDateTime expiryTime;
    private Object data;
}