package com.collage.student.entity;



import com.collage.student.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_records", indexes = {
        @Index(name = "idx_attendance_student", columnList = "studentId"),
        @Index(name = "idx_attendance_date", columnList = "attendanceDate"),
        @Index(name = "idx_attendance_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private String studentName;

    @Column(nullable = false)
    private String courseCode;

    @Column(nullable = false)
    private LocalDate attendanceDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status; // Present, Absent, Leave, Holiday

    @Column(nullable = false)
    private Long facultyId; // Faculty who marked attendance

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        log.debug("Attendance record updated for student: {}", this.studentId);
    }
}