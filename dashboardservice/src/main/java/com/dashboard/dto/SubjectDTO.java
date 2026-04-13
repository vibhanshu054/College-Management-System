package com.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectDTO {

    private String subjectId;
    private String subjectName;
    private String courseId;
    private String facultyId;
    private String scheduleTime;   // "10:00-11:30"
    private String day;            // "MONDAY"
}