package com.library.repository;
import com.library.entity.BookIssueEntity;
import com.library.enums.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface BookIssueRepository extends JpaRepository<BookIssueEntity, Long> {

    List<BookIssueEntity> findByUserId(String userId);

    List<BookIssueEntity> findByUserRole(String userRole);

    List<BookIssueEntity> findByUserRoleAndUserId(String userRole, String userId);

    List<BookIssueEntity> findByIssueDate(LocalDate issueDate);

    List<BookIssueEntity> findByReturnedDate(LocalDate returnedDate);
    List<BookIssueEntity> findByIssueDateOrReturnedDate(LocalDate issueDate, LocalDate returnedDate);

    List<BookIssueEntity> findByReturnedDateIsNullAndReturnableDateBefore(LocalDate date);

    long countByIssueDate(LocalDate date);

    long countByReturnedDate(LocalDate date);

    long countByUserIdAndStatus(String userId, IssueStatus status);

    @Query("select count(distinct b.userId) from BookIssueEntity b")
    long countDistinctUserId();

    boolean existsByBookIdAndUserIdAndReturnedDateIsNull(String bookId, String userId);

    long countByUserIdAndReturnedDateIsNull(String userId);

    List<BookIssueEntity> findByUserRoleIgnoreCase(String trim);
}