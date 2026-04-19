package com.student.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class ApiResponse {


    private String message;
    private int status;
    private Object data;
    private LocalDateTime timestamp;

//    private String universityId;
}