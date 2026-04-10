package com.userService.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class LibrarianDTO {
    private String universityId;
    private String librarianName;
    private String librarianEmail;
    private String librarianPhoneNumber;
}
