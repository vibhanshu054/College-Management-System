package com.userService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.NumberFormat;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String role;
    private String department;
    private String profilePictureUrl;
    private String bio;
    private String UniversityId;
    private Long studentServiceId;
    @NumberFormat(pattern = "\\d{10}")
    public String PhoneNumber;
}