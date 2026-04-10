package com.userService.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.userService.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private String department;

    // Phone number (standard format)
    @Column(nullable = false)
    private String phoneNumber;

    // 12-digit ID (unique per role)
    @Column(nullable = false, unique = true, length = 12)
    private String universityId;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Audit fields
    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    // Role-specific data (JSON stored as text)
    @Column(columnDefinition = "JSON")
    private String roleMetadata;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();

    }

    public Long studentServiceId;

    public Long facultyServiceId;

    public Long LibrarianServiceId;

    public String semester;

    public String batch;

    public String courseCode;

    public Object facultySubRole;
}