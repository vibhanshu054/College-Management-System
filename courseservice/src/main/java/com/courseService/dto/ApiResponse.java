package com.courseService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApiResponse {
    private String message;
    private int status;
    private LocalDateTime timestamp;
}