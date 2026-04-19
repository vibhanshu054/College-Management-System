package com.department_Service.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentDTO {
    private Long id;
    @NotBlank(message = "Department name is required")
    private String departmentName;
    @NotBlank(message = "Department code is required")
    private String departmentCode;
    private String hodId;
    private String hodName;
    private Integer totalFaculty;
    private Integer totalStudents;
    private String description;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}