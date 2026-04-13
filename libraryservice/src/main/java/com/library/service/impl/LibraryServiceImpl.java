package com.library.service.impl;

import com.library.client.StudentClient;
import com.library.dto.BookDTO;
import com.library.dto.BookIssueDTO;
import com.library.dto.LibrarianDTO;
import com.library.entity.BookEntity;
import com.library.entity.BookIssueEntity;
import com.library.entity.LibrarianEntity;
import com.library.enums.IssueStatus;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookIssueRepository;
import com.library.repository.BookRepository;
import com.library.repository.LibrarianRepository;
import com.library.service.LibraryService;
import com.library.validation.LibraryValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LibraryServiceImpl implements LibraryService {

    private final BookRepository bookRepository;
    private final BookIssueRepository bookIssueRepository;
    private final LibrarianRepository librarianRepository;
    private final StudentClient studentClient;
    private final LibraryValidation libraryValidation;
    private final ModelMapper modelMapper;
@Override
    public LibrarianDTO createLibrarian(LibrarianDTO librarianDTO) {
        log.info("Creating librarian with universityId: {}", librarianDTO.getUniversityId());

        librarianRepository.findByUniversityId(librarianDTO.getUniversityId())
                .ifPresent(l -> {
                    throw new IllegalArgumentException("Librarian already exists with universityId: " + librarianDTO.getUniversityId());
                });

        librarianRepository.findByLibrarianEmail(librarianDTO.getLibrarianEmail())
                .ifPresent(l -> {
                    throw new IllegalArgumentException("Librarian already exists with email: " + librarianDTO.getLibrarianEmail());
                });

        LibrarianEntity entity = modelMapper.map(librarianDTO, LibrarianEntity.class);
        if (entity.getCreatedBy() == null) {
            entity.setCreatedBy("SYSTEM");
        }
        if (entity.getUpdatedBy() == null) {
            entity.setUpdatedBy("SYSTEM");
        }

        LibrarianEntity saved = librarianRepository.save(entity);
        return modelMapper.map(saved, LibrarianDTO.class);
    }
    @Override
    public LibrarianDTO getLibrarian(Long id) {
        log.info("Fetching librarian by id: {}", id);

        LibrarianEntity librarian = librarianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Librarian not found with id: " + id));

        return modelMapper.map(librarian, LibrarianDTO.class);
    }
    @Override
    public BookDTO addBook(BookDTO bookDTO) {
        log.info("Adding book with bookId: {}", bookDTO.getBookId());

        libraryValidation.validateBookFields(bookDTO, false);

        BookEntity entity = modelMapper.map(bookDTO, BookEntity.class);
        entity.setIssuedCount(0);
        entity.setAvailableCount(bookDTO.getTotalCount());
        entity.setActive(true);

        BookEntity saved = bookRepository.save(entity);
        return modelMapper.map(saved, BookDTO.class);
    }
    @Override
    public List<BookDTO> getAllBooks(String searchTerm, boolean availableOnly) {
        log.info("Fetching all books with searchTerm: {} and availableOnly: {}", searchTerm, availableOnly);

        List<BookEntity> books;

        if (searchTerm != null && !searchTerm.isBlank()) {
            books = bookRepository.findByBookNameContainingIgnoreCaseOrAuthorContainingIgnoreCase(searchTerm, searchTerm);
        } else if (availableOnly) {
            books = bookRepository.findByAvailableCountGreaterThan(0);
        } else {
            books = bookRepository.findAll();
        }

        if (availableOnly && (searchTerm != null && !searchTerm.isBlank())) {
            books = books.stream()
                    .filter(book -> book.getAvailableCount() != null && book.getAvailableCount() > 0)
                    .toList();
        }

        return modelMapper.map(books, new TypeToken<List<BookDTO>>() {}.getType());
    }
    @Override
    public BookDTO getBookById(String bookId) {
        log.info("Fetching book by bookId: {}", bookId);

        BookEntity book = libraryValidation.validateBookExists(bookId);
        return modelMapper.map(book, BookDTO.class);
    }
    @Override
    public BookDTO updateBook(String bookId, BookDTO bookDTO) {
        log.info("Updating book with bookId: {}", bookId);

        BookEntity existing = libraryValidation.validateBookExists(bookId);
        libraryValidation.validateBookFields(bookDTO, true);

        if (bookDTO.getBookName() != null) {
            existing.setBookName(bookDTO.getBookName());
        }
        if (bookDTO.getAuthor() != null) {
            existing.setAuthor(bookDTO.getAuthor());
        }
        if (bookDTO.getPublication() != null) {
            existing.setPublication(bookDTO.getPublication());
        }
        if (bookDTO.getDescription() != null) {
            existing.setDescription(bookDTO.getDescription());
        }
        if (bookDTO.getTotalCount() != null) {
            if (bookDTO.getTotalCount() < existing.getIssuedCount()) {
                throw new IllegalArgumentException("Total count cannot be less than issued count");
            }
            existing.setTotalCount(bookDTO.getTotalCount());
            existing.setAvailableCount(existing.getTotalCount() - existing.getIssuedCount());
        }

        BookEntity updated = bookRepository.save(existing);
        return modelMapper.map(updated, BookDTO.class);
    }
    @Override
    public void deleteBook(String bookId) {
        log.info("Deleting book with bookId: {}", bookId);

        BookEntity existing = libraryValidation.validateBookExists(bookId);

        if (existing.getIssuedCount() != null && existing.getIssuedCount() > 0) {
            throw new IllegalArgumentException("Cannot delete a book that is currently issued");
        }

        bookRepository.delete(existing);
    }
    @Override
    public BookIssueDTO issueBook(BookIssueDTO bookIssueDTO) {
        log.info("Issuing bookId: {} to userId: {}", bookIssueDTO.getBookId(), bookIssueDTO.getUserId());

        libraryValidation.validateIssueRequest(bookIssueDTO);

        if ("STUDENT".equalsIgnoreCase(bookIssueDTO.getUserRole())) {
            try {
                studentClient.getStudentById(Long.valueOf(bookIssueDTO.getUserId()));
            } catch (Exception ex) {
                throw new IllegalArgumentException("Student not found with id: " + bookIssueDTO.getUserId());
            }
        }

        BookEntity book = libraryValidation.validateBookExists(bookIssueDTO.getBookId());
        libraryValidation.validateBookAvailability(book);

        book.setIssuedCount(book.getIssuedCount() + 1);
        book.setAvailableCount(book.getTotalCount() - book.getIssuedCount());
        bookRepository.save(book);

        BookIssueEntity issueEntity = new BookIssueEntity();
        issueEntity.setBookId(book.getBookId());
        issueEntity.setBookName(book.getBookName());
        issueEntity.setUserId(bookIssueDTO.getUserId());
        issueEntity.setUserRole(bookIssueDTO.getUserRole().toUpperCase());
        issueEntity.setUserName(bookIssueDTO.getUserName());
        issueEntity.setIssueDate(LocalDate.now());
        issueEntity.setReturnableDate(
                bookIssueDTO.getReturnableDate() != null ? bookIssueDTO.getReturnableDate() : LocalDate.now().plusDays(14)
        );
        issueEntity.setStatus(IssueStatus.ISSUED);
        issueEntity.setFineAmount(0);
        issueEntity.setFinePaid(false);

        BookIssueEntity saved = bookIssueRepository.save(issueEntity);
        return modelMapper.map(saved, BookIssueDTO.class);
    }
    @Override
    public BookIssueDTO returnBook(Long issueId) {
        log.info("Returning book for issueId: {}", issueId);

        BookIssueEntity issueEntity = libraryValidation.validateIssueRecordExists(issueId);
        libraryValidation.validateReturnable(issueEntity);

        BookEntity book = libraryValidation.validateBookExists(issueEntity.getBookId());

        issueEntity.setReturnedDate(LocalDate.now());
        issueEntity.setStatus(IssueStatus.RETURNED);

        if (issueEntity.getReturnableDate() != null && issueEntity.getReturnedDate().isAfter(issueEntity.getReturnableDate())) {
            long overdueDays = ChronoUnit.DAYS.between(issueEntity.getReturnableDate(), issueEntity.getReturnedDate());
            issueEntity.setFineAmount((int) (overdueDays * 5));
        } else {
            issueEntity.setFineAmount(0);
        }

        book.setIssuedCount(Math.max(0, book.getIssuedCount() - 1));
        book.setAvailableCount(book.getTotalCount() - book.getIssuedCount());

        bookRepository.save(book);
        BookIssueEntity updated = bookIssueRepository.save(issueEntity);

        return modelMapper.map(updated, BookIssueDTO.class);
    }
    @Override
    public List<BookIssueDTO> getIssueRecords(String userRole, String userId) {
        log.info("Fetching issue records for userRole: {} and userId: {}", userRole, userId);

        List<BookIssueEntity> issues;

        if (userId != null && !userId.isBlank()) {
            issues = bookIssueRepository.findByUserId(userId);
        } else if (userRole != null && !userRole.isBlank()) {
            issues = bookIssueRepository.findByUserRoleIgnoreCase(userRole);
        } else {
            issues = bookIssueRepository.findAll();
        }

        return modelMapper.map(issues, new TypeToken<List<BookIssueDTO>>() {}.getType());
    }
    @Override
    public List<Map<String, Object>> getOverdueBooks() {
        log.info("Fetching overdue books");

        List<BookIssueEntity> overdueIssues = bookIssueRepository
                .findByReturnedDateIsNullAndReturnableDateBefore(LocalDate.now());

        return overdueIssues.stream().map(issue -> {
            Map<String, Object> map = new HashMap<>();
            map.put("issueId", issue.getId());
            map.put("bookId", issue.getBookId());
            map.put("bookName", issue.getBookName());
            map.put("userId", issue.getUserId());
            map.put("userName", issue.getUserName());
            map.put("userRole", issue.getUserRole());
            map.put("issueDate", issue.getIssueDate());
            map.put("returnableDate", issue.getReturnableDate());
            map.put("daysOverdue", ChronoUnit.DAYS.between(issue.getReturnableDate(), LocalDate.now()));
            map.put("fineAmount", issue.getFineAmount());
            return map;
        }).toList();
    }
    @Override
    public Map<String, Object> getLibraryDashboard() {
        log.info("Fetching library dashboard");

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("totalBooks", bookRepository.count());
        dashboard.put("availableBooks", bookRepository.findByAvailableCountGreaterThan(0).size());
        dashboard.put("issuedBooks", bookIssueRepository.findByStatus(IssueStatus.ISSUED).size());
        dashboard.put("returnedBooks", bookIssueRepository.findByStatus(IssueStatus.RETURNED).size());
        dashboard.put("overdueBooks", bookIssueRepository.findByReturnedDateIsNullAndReturnableDateBefore(LocalDate.now()).size());
        dashboard.put("totalLibrarians", librarianRepository.count());
        return dashboard;
    }

    public Map<String, Integer> getTotalBooksCount() {
        return Map.of("totalBooks", (int) bookRepository.count());
    }

    public Map<String, Integer> getAvailableBooksCount() {
        return Map.of("availableBooks", bookRepository.findByAvailableCountGreaterThan(0).size());
    }

    public Map<String, Object> getTodayActivity() {
        log.info("Fetching today's activity");

        Map<String, Object> todayActivity = new HashMap<>();
        todayActivity.put("todayIssued", bookIssueRepository.countByIssueDate(LocalDate.now()));
        todayActivity.put("todayReturned", bookIssueRepository.countByReturnedDate(LocalDate.now()));
        return todayActivity;
    }

    public Map<String, Object> getTotalMembers() {
        log.info("Fetching total members count");
        Map<String, Object> result = new HashMap<>();
        result.put("totalMembers", bookIssueRepository.findAll()
                .stream()
                .map(BookIssueEntity::getUserId)
                .distinct()
                .count());
        return result;
    }
}