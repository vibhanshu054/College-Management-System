package com.courseService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "faculty_course_mapping")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacultyCourseMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String facultyUniversityId;   // universityId

    @Column(nullable = false)
    private String courseCode;

    @Column(nullable = false)
    private String courseName;
}