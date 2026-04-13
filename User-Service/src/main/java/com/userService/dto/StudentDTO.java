package com.userService.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentDTO {

    private Long id;

    @NotBlank(message = "Student name is required")
    private String studentName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String studentEmail;

    @NotBlank(message = "Phone number is required")
    private String studentPhoneNumber;

    @NotBlank(message = "University ID is required")
    private String universityId;

    private String semester;
    private String batch;
    private String department;
    private String course;
    private String courseCode;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}