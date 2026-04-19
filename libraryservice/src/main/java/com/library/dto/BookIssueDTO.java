package com.library.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.library.enums.IssueStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookIssueDTO {

    private Long id;
    private String bookId;
    private String bookName;
    private String userId;
    private String userName;
    private String userEmail;
    private String department;
    private String course;
    private String userRole;
    private LocalDate issueDate;
    private LocalDate returnableDate;
    private LocalDate returnedDate;
    private Boolean activeIssue;
    private IssueStatus status;
}