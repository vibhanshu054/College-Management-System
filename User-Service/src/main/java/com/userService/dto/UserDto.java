package com.userService.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.NumberFormat;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private Long id;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Role is required")
    private String role;  // ADMIN, FACULTY, LIBRARIAN, STUDENT

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "University ID is required")
    private String universityId;


    // Optional for students
    private String semester;
    private String batch;
    private String courseCode;

    // Optional for faculty
    private String facultySubRole;  // HOD, PROFESSOR, ASSISTANT_PROFESSOR, TRAINEE

    private boolean active= true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}