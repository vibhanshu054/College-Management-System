package com.collage.student.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "students", indexes = {
        @Index(name = "idx_student_id", columnList = "universityId"),
        @Index(name = "idx_student_email", columnList = "email"),
        @Index(name = "idx_student_course", columnList = "courseId")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Database Identity
    @Column(nullable = false, unique = true, length = 12)
    private String universityId;  // 12-digit student ID

    @Column(nullable = false)
    private String studentName;

    @Column(nullable = false, unique = true)
    private String studentEmail;

    @Column(nullable = false)
    private String studentPhoneNumber;

    @Column(nullable = false)
    private String semester;

    @Column(nullable = false)
    private String batch;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String course;

    @Column(nullable = false)
    private String courseCode;

    // Faculty ID (Foreign Reference - READ ONLY)
    @Column(name = "facultyId", nullable = false)
    private String facultyId;

    @Column(name = "faculty_name")
    private String facultyName;

    // Attendance Calendar (JSON or separate table)
    @Column(columnDefinition = "JSON")
    private String attendanceCalendar;

    @Column(name = "attendance_percentage", columnDefinition = "FLOAT DEFAULT 0.0")
    private Float attendancePercentage = 0.0f;

    // Books issued and returned count
    @Column(name = "books_issued", columnDefinition = "INT DEFAULT 0")
    private Integer booksIssued = 0;

    @Column(name = "books_returned", columnDefinition = "INT DEFAULT 0")
    private Integer booksReturned = 0;

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

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}