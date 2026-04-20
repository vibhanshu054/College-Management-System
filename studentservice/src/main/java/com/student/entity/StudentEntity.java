package com.student.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "students",
        indexes = {
                @Index(name = "idx_student_university_id", columnList = "university_id"),
                @Index(name = "idx_student_email", columnList = "student_email"),
                @Index(name = "idx_student_course_code", columnList = "course_code")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "university_id", unique = true, length = 12)
    private String universityId;

    @Column(name = "password")
    private String password;

    @Column(name = "student_name", nullable = false, length = 100)
    private String studentName;

    @Column(name = "student_email", nullable = false, unique = true, length = 150)
    private String studentEmail;

    @Column(name = "student_phone_number", nullable = false, length = 20)
    private String studentPhoneNumber;

    @Column(name = "semester", nullable = false, length = 20)
    private String semester;

    @Column(name = "batch", nullable = false, length = 30)
    private String batch;

    @Column(name = "department", nullable = false, length = 100)
    private String department;

    @Column(name = "course", nullable = false, length = 100)
    private String course;

    @Column(name = "course_code", nullable = false, length = 20)
    private String courseCode;

    @Column(name = "faculty_university_id")
    private String facultyUniversityId;

    @Column(name = "faculty_name", length = 100)
    private String facultyName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "subjects", columnDefinition = "json")
    private List<String> subjects;

    @Column(name = "attendance_calendar", columnDefinition = "json")
    private String attendanceCalendar;

    @Column(name = "attendance_percentage", nullable = false)
    private Float attendancePercentage = 0.0f;

    @Column(name = "books_issued", nullable = false)
    private Integer booksIssued = 0;

    @Column(name = "books_returned", nullable = false)
    private Integer booksReturned = 0;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;



    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.booksIssued == null) this.booksIssued = 0;
        if (this.booksReturned == null) this.booksReturned = 0;
        if (this.attendancePercentage == null) this.attendancePercentage = 0.0f;
        if (this.subjects == null) this.subjects = new ArrayList<>();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.subjects == null) this.subjects = new ArrayList<>();
    }
}