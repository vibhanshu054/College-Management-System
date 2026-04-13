package com.library.repository;

import com.library.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<BookEntity, Long> {

    Optional<BookEntity> findByBookId(String bookId);

    boolean existsByBookNameIgnoreCaseAndAuthorIgnoreCase(String bookName, String author);

    List<BookEntity> findByBookNameContainingIgnoreCaseOrAuthorContainingIgnoreCase(String bookName, String author);

    List<BookEntity> findByAvailableCountGreaterThan(Integer count);
}