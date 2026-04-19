package com.subject_Service.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "subjects", indexes = {
        @Index(name = "idx_subject_code", columnList = "subjectCode"),
        @Index(name = "idx_subject_name", columnList = "subjectName"),
        @Index(name = "idx_course_id", columnList = "courseId")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "createdAt")
public class SubjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String subjectCode;

    @Column(nullable = false, length = 255)
    private String subjectName;

    @Column(nullable = false)
    private String courseId;
    @Column(nullable = false)
    private String courseCode;

    @Column(nullable = false)
    private String courseName;

    @Column(nullable = false)
    private String departmentId;

    @Column(nullable = false)
    private String departmentCode;

    @Column(nullable = false)
    private Integer credits = 0;

    @Column(nullable = false)
    private Integer semester = 0;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String courseObjectives;

    @Column(columnDefinition = "TEXT")
    private String outcomes;

    // Faculty assigned to this subject
    @Column(nullable = true)
    private String facultyId;

    @Column(nullable = true)
    private String facultyName;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer totalStudentsEnrolled = 0;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

   private String studentUniversityId;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}