package com.facultyService.dto;

import com.facultyService.enums.FacultySubRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FacultyDTO {

    private Long id;
    private String universityId;
    private String facultyName;
    private String facultyEmail;
    private String facultyPhoneNumber;
    private String department;
    private FacultySubRole subRole;
    private String attendanceCalendar;
    private Float attendancePercentage;
    private Integer booksIssued;
    private Integer booksReturned;
    private String scheduleData;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}