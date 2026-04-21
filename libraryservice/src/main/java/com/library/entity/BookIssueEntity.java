package com.library.entity;

import com.library.enums.IssueStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(
        name = "book_issues",
        indexes = {
                @Index(name = "idx_issue_book_id", columnList = "book_id"),
                @Index(name = "idx_issue_user_id", columnList = "user_id"),
                @Index(name = "idx_issue_status", columnList = "status"),
                @Index(name = "idx_issue_active_issue", columnList = "active_issue")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookIssueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "book_id", nullable = false, length = 50)
    private String bookId;

    @Column(name = "book_name", nullable = false, length = 200)
    private String bookName;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "user_role", nullable = false, length = 30)
    private String userRole;

    @Column(name = "user_name", nullable = false, length = 150)
    private String userName;

    @Column(name = "user_email", length = 150)
    private String userEmail;

    @Column(name = "department", length = 150)
    private String department;

    @Column(name = "course", length = 150)
    private String course;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "returnable_date", nullable = false)
    private LocalDate returnableDate;

    @Column(name = "returned_date")
    private LocalDate returnedDate;

    @Column(name = "active_issue", nullable = false)
    private Boolean activeIssue = true;

    @Column(name = "active_return", nullable = false)
    private Boolean activeReturn = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private IssueStatus status = IssueStatus.ISSUED;

    @Column(name = "fine_amount", nullable = false)
    private Integer fineAmount = 0;

    @Column(name = "fine_paid", nullable = false)
    private Boolean finePaid = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;

        if (activeIssue == null) {
            activeIssue = true;
        }
        if (status == null) {
            status = IssueStatus.ISSUED;
        }
        if (fineAmount == null) {
            fineAmount = 0;
        }
        if (finePaid == null) {
            finePaid = false;
        }

        updateFineIfNeeded();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
        updateFineIfNeeded();
    }

    private void updateFineIfNeeded() {
        if (status == IssueStatus.RETURNED) {
            if (fineAmount == null) {
                fineAmount = 0;
            }
            return;
        }

        if (returnableDate != null && LocalDate.now().isAfter(returnableDate)) {
            long daysOverdue = ChronoUnit.DAYS.between(returnableDate, LocalDate.now());
            fineAmount = Math.toIntExact(daysOverdue * 5);
        } else {
            fineAmount = 0;
        }
    }
}