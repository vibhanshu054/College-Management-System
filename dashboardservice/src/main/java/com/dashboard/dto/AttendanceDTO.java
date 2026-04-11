package com.college.attendance_Service.dto;



import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttendanceDTO {

    private Long id;

    @NotBlank(message = "Student ID is required")
    private String studentId;

    private String studentName;
    private String studentEmail;

    @NotBlank(message = "Course ID is required")
    private String courseId;

    private String courseName;
    private String courseCode;

    @NotNull(message = "Attendance date is required")
    private LocalDate attendanceDate;

    @NotNull(message = "Attendance status is required")
    private String status;

    @NotBlank(message = "Faculty ID is required")
    private String facultyId;

    private String facultyName;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}