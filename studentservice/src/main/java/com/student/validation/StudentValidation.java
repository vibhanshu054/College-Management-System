package com.student.validation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StudentValidation {

    @NotBlank(message = "University ID is required")
    @Size(min = 12, max = 12, message = "University ID must be exactly 12 characters")
    private String universityId;

    @NotBlank(message = "Student name is required")
    @Size(min = 2, max = 100, message = "Student name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Student name must contain only letters and spaces")
    private String studentName;

    @NotBlank(message = "Student email is required")
    @Email(message = "Student email must be a valid email address")
    @Size(max = 150, message = "Student email must not exceed 150 characters")
    private String studentEmail;

    @NotBlank(message = "Phone number is required")
    private String studentPhoneNumber;

    @NotBlank(message = "Semester is required")
    private String semester;

    @NotBlank(message = "Batch is required")
    private String batch;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Course is required")
    private String course;

    @NotBlank(message = "Course code is required")
    private String courseCode;

    @NotBlank(message = "Faculty ID is required")
    private String facultyId;
}