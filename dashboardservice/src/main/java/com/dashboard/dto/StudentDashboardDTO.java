package com.collage.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

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
    private List<Map<String, Object>> enrolledSubjects;
}