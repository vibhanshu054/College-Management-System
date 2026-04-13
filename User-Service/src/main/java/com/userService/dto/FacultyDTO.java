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
public class FacultyDTO {

    private Long id;

    @NotBlank(message = "Faculty name is required")
    private String facultyName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String facultyEmail;

    @NotBlank(message = "Phone number is required")
    private String facultyPhoneNumber;

    @NotBlank(message = "University ID is required")
    private String universityId;

    private String department;
    private String subRole;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
