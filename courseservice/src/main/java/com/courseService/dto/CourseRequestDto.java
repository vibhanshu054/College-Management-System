package com.courseService.dto;



import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CourseRequestDto {


    @NotBlank(message = "Course name is required")
    private String name;
    @NotBlank(message = "CourseCode is required")
    private String courseCode;

}