package com.facultyService.dto;

import com.facultyService.enums.FacultySubRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FacultyDTO {

    private Long id;
    private String facultyUniversityId;
    private String facultyName;
    private String facultyEmail;
    private String username;
    private String password;
    private String facultyPhoneNumber;
    private String department;
    private FacultySubRole subRole;
    private List<String> courseCode;
    private Integer totalCourse;
    private Float attendancePercentage;
    private Integer booksIssued;
    private Integer booksReturned;
    private Map<String, Object> attendanceCalendar;
    private Map<String, Object> scheduleData;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}