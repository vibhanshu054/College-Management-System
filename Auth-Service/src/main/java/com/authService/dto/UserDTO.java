package com.authService.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    private Long id;
    private String email;
    private String username;
    private String password;
    private String role;
    private String department;
    private String phoneNumber;
    private String universityId;
    private boolean active;
}