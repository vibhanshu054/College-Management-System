package com.facultyService.entity;

import com.facultyService.enums.FacultyAttendanceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "faculty_attendance", indexes = {
        @Index(name = "idx_fac_att_id", columnList = "facultyId"),
        @Index(name = "idx_fac_att_date", columnList = "attendanceDate")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class FacultyAttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long facultyId;

    @Column(nullable = false)
    private String facultyName;

    @Column(nullable = false)
    private LocalDate attendanceDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FacultyAttendanceStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
