package com.library.entity;

import com.library.enums.IssueStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "book_issues", indexes = {
        @Index(name = "idx_issue_book_id", columnList = "bookId"),
        @Index(name = "idx_issue_user_id", columnList = "userId"),
        @Index(name = "idx_issue_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookIssueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bookId;

    @Column(nullable = false)
    private String bookName;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String userRole;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false)
    private LocalDate returnableDate;

    private LocalDate returnedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueStatus status = IssueStatus.ISSUED;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer fineAmount = 0;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean finePaid = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (fineAmount == null) {
            fineAmount = 0;
        }
        if (finePaid == null) {
            finePaid = false;
        }
        if (status == null) {
            status = IssueStatus.ISSUED;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();

        if (status == IssueStatus.ISSUED && returnableDate != null && LocalDate.now().isAfter(returnableDate)) {
            long daysOverdue = ChronoUnit.DAYS.between(returnableDate, LocalDate.now());
            fineAmount = (int) (daysOverdue * 5);
        }
    }
}