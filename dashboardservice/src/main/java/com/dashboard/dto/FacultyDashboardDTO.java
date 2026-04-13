package com.dashboard.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacultyDashboardDTO {
    private String facultyName;
    private int totalClassesAssigned;
    private int totalStudents;
    private double attendancePercentage;
    private List<Map<String, Object>> todaySchedule;
    private Map<String, Object> bookStats;
}