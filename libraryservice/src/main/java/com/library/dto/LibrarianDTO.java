package com.library.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LibrarianDTO {

    private Long id;
    private String universityId;
    private String librarianName;
    private String librarianEmail;
    private String librarianPhoneNumber;
    private Map<String, Object> attendanceCalendar;
    private Float attendancePercentage;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}