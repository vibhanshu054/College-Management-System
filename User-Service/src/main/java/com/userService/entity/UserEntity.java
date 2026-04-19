package com.userService.entity;


import com.userService.enums.FacultySubRole;
import com.userService.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_university_id", columnList = "universityId"),
        @Index(name = "idx_user_role", columnList = "role")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"createdAt", "password"})
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String username;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private UserRole role;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(nullable = false, length = 15)
    private String phoneNumber;

    @Column(nullable = false, unique = true, length = 12)
    private String universityId;

    // Optional fields for students
    @Column(nullable = true, length = 50)
    private String semester;

    @Column(nullable = true, length = 50)
    private String batch;

    @Column(nullable = true, length = 50)
    private String courseCode;

    // Optional fields for faculty
    @Enumerated(EnumType.STRING)
    @Column(nullable = true, length = 50)
    private FacultySubRole facultySubRole;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "created_by", nullable = true)
    private String createdBy;

    @Column(name = "updated_by", nullable = true)
    private String updatedBy;

    // Service IDs for cascade mapping
    @Column(nullable = true)
    private String studentServiceUniversityId;

    @Column(nullable = true)
    private String facultyServiceUniversityId;

    @Column(nullable = true)
    private String librarianServiceUniversityId;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}