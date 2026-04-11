package com.userService.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class StudentDto {
    private String id;
    private String studentName;
    private String studentEmail;
    private String universityId;
    private String phoneNumber;
    private String semester;
    private String batch;
    private String department;
    private String courseCode;
}
