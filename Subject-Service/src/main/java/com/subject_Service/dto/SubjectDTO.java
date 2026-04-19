package com.subject_Service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubjectDTO {

    private Long id;

    @NotBlank(message = "Subject code is required")
    private String subjectCode;

    @NotBlank(message = "Subject name is required")
    private String subjectName;

    @NotBlank(message = "Course ID is required")
    private String courseId;

    @NotBlank(message = "Course Code is required")
    private String courseCode;

    private String courseName;

    @NotBlank(message = "Department ID is required")
    private String departmentId;
    @NotBlank(message = "Department Code is required")
    private String departmentCode;

    @Min(value = 1, message = "Credits must be at least 1")
    private Integer credits;

    @Min(value = 1, message = "Semester must be at least 1")
    private Integer semester;
   private String studentUniversityId;
    private String description;
    private String courseObjectives;
    private String outcomes;
    private String facultyId;
    private String facultyName;
    private Integer totalStudentsEnrolled;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}