package com.courseService.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Entity
@Data
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Course name is required")
    @Column(nullable = false, unique = true)
    @Size(min = 3, max = 100, message = "Course name must be between 3 and 100 characters")
    private String name;

    @NotBlank(message = "Course code is required")
    @Column(nullable = false, unique = true)
    @Pattern(regexp = "^[A-Z]{2,5}\\d{3,6}$", message = "Course code must be like 'CS101' or 'MATH2001'")
    private String code;
}