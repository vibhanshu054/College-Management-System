package com.college.department_Service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "departments", indexes = {
        @Index(name = "idx_dept_name", columnList = "departmentName"),
        @Index(name = "idx_dept_code", columnList = "departmentCode")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "createdAt")
public class DepartmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String departmentName;

    @Column(nullable = false, unique = true, length = 50)
    private String departmentCode;

    @Column(nullable = false)
    private String hodId;

    @Column(nullable = false)
    private String hodName;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer totalFaculty = 0;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer totalStudents = 0;

    @Column(columnDefinition = "TEXT")
    private String description;

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
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}