package com.library.entity;

import com.library.config.MapToJsonConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "librarians", indexes = {
        @Index(name = "idx_librarian_id", columnList = "universityId"),
        @Index(name = "idx_librarian_email", columnList = "librarianEmail")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LibrarianEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 12)
    private String universityId;

    @Column(nullable = false)
    private String librarianName;

    @Column(nullable = false, unique = true)
    private String librarianEmail;

    @Column(nullable = false)
    private String librarianPhoneNumber;

    @Column(columnDefinition = "JSON")
    @Convert(converter = MapToJsonConverter.class)
    private Map<String, Object> attendanceCalendar;

    @Column(name = "attendance_percentage", columnDefinition = "FLOAT DEFAULT 0.0")
    private Float attendancePercentage = 0.0f;

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
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (attendancePercentage == null) {
            attendancePercentage = 0.0f;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}