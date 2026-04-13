package com.library.validation;

import com.library.dto.BookDTO;
import com.library.dto.BookIssueDTO;
import com.library.entity.BookEntity;
import com.library.entity.BookIssueEntity;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookIssueRepository;
import com.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LibraryValidation {

    private static final int MAX_BOOKS_PER_USER = 3;
    private static final int MAX_NAME_LENGTH = 100;
    private static final String NAME_PATTERN = "^[a-zA-Z0-9 .,:'\\-]+$";
    private static final String AUTHOR_PATTERN = "^[a-zA-Z .'-]+$";

    private final BookRepository bookRepository;
    private final BookIssueRepository bookIssueRepository;

    public void validateBookFields(BookDTO bookDTO, boolean isUpdate) {
        if (bookDTO == null) {
            throw new IllegalArgumentException("Book data must not be null");
        }

        if (!isUpdate && (bookDTO.getBookId() == null || bookDTO.getBookId().isBlank())) {
            throw new IllegalArgumentException("Book ID must not be blank");
        }

        if (!isUpdate && bookRepository.findByBookId(bookDTO.getBookId()).isPresent()) {
            throw new IllegalArgumentException("Book already exists with ID: " + bookDTO.getBookId());
        }

        if (bookDTO.getBookName() == null || bookDTO.getBookName().isBlank()) {
            throw new IllegalArgumentException("Book name must not be blank");
        }
        if (bookDTO.getBookName().trim().length() < 2) {
            throw new IllegalArgumentException("Book name must be at least 2 characters");
        }
        if (bookDTO.getBookName().length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Book name must not exceed " + MAX_NAME_LENGTH + " characters");
        }
        if (!bookDTO.getBookName().matches(NAME_PATTERN)) {
            throw new IllegalArgumentException("Book name contains invalid characters");
        }

        if (bookDTO.getAuthor() == null || bookDTO.getAuthor().isBlank()) {
            throw new IllegalArgumentException("Author name must not be blank");
        }
        if (bookDTO.getAuthor().trim().length() < 2) {
            throw new IllegalArgumentException("Author name must be at least 2 characters");
        }
        if (bookDTO.getAuthor().length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Author name must not exceed " + MAX_NAME_LENGTH + " characters");
        }
        if (!bookDTO.getAuthor().matches(AUTHOR_PATTERN)) {
            throw new IllegalArgumentException("Author name contains invalid characters");
        }

        if (bookDTO.getPublication() == null || bookDTO.getPublication().isBlank()) {
            throw new IllegalArgumentException("Publication must not be blank");
        }

        if (bookDTO.getTotalCount() == null || bookDTO.getTotalCount() < 0) {
            throw new IllegalArgumentException("Total count must be zero or greater");
        }

        if (!isUpdate && bookRepository.existsByBookNameIgnoreCaseAndAuthorIgnoreCase(
                bookDTO.getBookName().trim(),
                bookDTO.getAuthor().trim())) {
            throw new IllegalArgumentException(
                    "Book '" + bookDTO.getBookName() + "' by '" + bookDTO.getAuthor() + "' already exists"
            );
        }
    }

    public BookEntity validateBookExists(String bookId) {
        if (bookId == null || bookId.isBlank()) {
            throw new IllegalArgumentException("Book ID must not be blank");
        }

        return bookRepository.findByBookId(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + bookId));
    }

    public void validateBookAvailability(BookEntity book) {
        if (book.getAvailableCount() == null || book.getAvailableCount() <= 0) {
            throw new IllegalArgumentException("Book is currently not available for issuing");
        }
    }

    public void validateIssueRequest(BookIssueDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Issue request must not be null");
        }

        if (dto.getBookId() == null || dto.getBookId().isBlank()) {
            throw new IllegalArgumentException("Book ID is required");
        }

        if (dto.getUserId() == null || dto.getUserId().isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (dto.getUserRole() == null || dto.getUserRole().isBlank()) {
            throw new IllegalArgumentException("User role is required");
        }

        if (dto.getUserName() == null || dto.getUserName().isBlank()) {
            throw new IllegalArgumentException("User name is required");
        }

        if (bookIssueRepository.existsByBookIdAndUserIdAndReturnedDateIsNull(dto.getBookId(), dto.getUserId())) {
            throw new IllegalArgumentException("This user already has this book issued and not yet returned");
        }

        long activeBooks = bookIssueRepository.countByUserIdAndReturnedDateIsNull(dto.getUserId());
        if (activeBooks >= MAX_BOOKS_PER_USER) {
            throw new IllegalArgumentException("User has already reached the maximum issue limit of " + MAX_BOOKS_PER_USER);
        }
    }

    public BookIssueEntity validateIssueRecordExists(Long issueId) {
        if (issueId == null || issueId <= 0) {
            throw new IllegalArgumentException("Issue ID must be a positive number");
        }

        return bookIssueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue record not found with ID: " + issueId));
    }

    public void validateReturnable(BookIssueEntity issueEntity) {
        if (issueEntity.getReturnedDate() != null) {
            throw new IllegalArgumentException("Book is already returned for issue ID: " + issueEntity.getId());
        }
    }
}