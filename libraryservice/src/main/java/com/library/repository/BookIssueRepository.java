package com.library.repository;

import com.library.entity.BookIssueEntity;
import com.library.enums.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookIssueRepository extends JpaRepository<BookIssueEntity, Long> {

    boolean existsByBookIdAndUserIdAndReturnedDateIsNull(String bookId, String userId);

    long countByUserIdAndReturnedDateIsNull(String userId);

    List<BookIssueEntity> findByUserRoleIgnoreCase(String userRole);

    List<BookIssueEntity> findByUserId(String userId);

    List<BookIssueEntity> findByStatus(IssueStatus status);

    List<BookIssueEntity> findByReturnedDateIsNullAndReturnableDateBefore(LocalDate date);

    long countByIssueDate(LocalDate issueDate);

    long countByReturnedDate(LocalDate returnedDate);
}