package com.student.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "students", indexes = {
        @Index(name = "idx_student_university_id", columnList = "universityId"),
        @Index(name = "idx_student_email", columnList = "studentEmail"),
        @Index(name = "idx_student_course_code", columnList = "courseCode")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 12)
    private String universityId;

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

    @Column(name = "facultyId", nullable = false)
    private String facultyId;

    @Column(name = "faculty_name")
    private String facultyName;

    @Column(columnDefinition = "JSON")
    private String attendanceCalendar;

    @Column(name = "attendance_percentage", columnDefinition = "FLOAT DEFAULT 0.0")
    private Float attendancePercentage = 0.0f;

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

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.booksIssued == null) {
            this.booksIssued = 0;
        }
        if (this.booksReturned == null) {
            this.booksReturned = 0;
        }
        if (this.attendancePercentage == null) {
            this.attendancePercentage = 0.0f;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}