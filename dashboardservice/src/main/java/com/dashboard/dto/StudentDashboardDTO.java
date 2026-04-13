package com.dashboard.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDashboardDTO {

    private String studentName;
    private String courseName;
    private double attendancePercentage;
    private int booksIssued;
    private int booksReturned;
    private int totalClasses;

    private List<SubjectDTO> enrolledSubjects;
}