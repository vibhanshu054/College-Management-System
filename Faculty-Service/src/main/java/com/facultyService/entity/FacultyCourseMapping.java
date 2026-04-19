package com.facultyService.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "faculty_course_mapping")
public class FacultyCourseMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "faculty_university_id", nullable = false)
    private String facultyUniversityId;

    @Column(name = "course_code", nullable = false)
    private String courseCode;

    @Column(name = "course_name")
    private String courseName;
}