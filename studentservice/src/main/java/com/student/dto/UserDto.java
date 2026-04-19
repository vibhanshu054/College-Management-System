package com.student.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private String email;
    private String username;
    private String name;
    private String password;
    private String role;
    private String department;
    private String phoneNumber;
    private String universityId;

    private String semester;
    private String batch;
    private String courseCode;

}