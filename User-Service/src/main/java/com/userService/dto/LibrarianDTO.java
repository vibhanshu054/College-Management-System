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
public class LibrarianDTO {

    private Long id;

    @NotBlank(message = "Librarian name is required")
    private String librarianName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String librarianEmail;

    @NotBlank(message = "Phone number is required")
    private String librarianPhoneNumber;

    @NotBlank(message = "University ID is required")
    private String universityId;

    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
