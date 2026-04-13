package com.userService.entity;

import com.userService.enums.FacultySubRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "faculty", indexes = {
        @Index(name = "idx_faculty_id", columnList = "universityId"),
        @Index(name = "idx_faculty_email", columnList = "facultyEmail"),
        @Index(name = "idx_faculty_dept", columnList = "department")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacultyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 12)
    private String universityId;

    @Column(nullable = false)
    private String facultyName;

    @Column(nullable = false, unique = true)
    private String facultyEmail;

    @Column(nullable = false)
    private String facultyPhoneNumber;

    @Column(nullable = false)
    private String department;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FacultySubRole subRole;

    @Column(columnDefinition = "JSON")
    private String attendanceCalendar;

    @Column(name = "attendance_percentage")
    private Float attendancePercentage = 0.0f;

    @Column(name = "books_issued")
    private Integer booksIssued = 0;

    @Column(name = "books_returned")
    private Integer booksReturned = 0;

    @Column(columnDefinition = "JSON")
    private String scheduleData;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.active == null) {
            this.active = true;
        }
        if (this.attendancePercentage == null) {
            this.attendancePercentage = 0.0f;
        }
        if (this.booksIssued == null) {
            this.booksIssued = 0;
        }
        if (this.booksReturned == null) {
            this.booksReturned = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}