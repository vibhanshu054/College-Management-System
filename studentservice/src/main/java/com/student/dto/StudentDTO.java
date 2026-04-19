package com.student.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentDTO {

    private Long id;
    private String universityId;
    private String studentName;
    private String studentEmail;
    private String studentPhoneNumber;
    private String semester;
    private String batch;
    private String department;
    private String course;
    private String courseCode;
    private String facultyUniversityId;
    private String facultyName;
    private Integer booksIssued;
    private Integer booksReturned;
    private Float attendancePercentage;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}