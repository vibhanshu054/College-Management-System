package com.userService.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class FacultyDTO {
    private String universityId;
    private String facultyName;
    private String facultyEmail;
    private String facultyPhoneNumber;
    private String department;
    private String subRole;

}
