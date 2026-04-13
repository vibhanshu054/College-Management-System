package com.library.controller;

import com.library.dto.BookDTO;
import com.library.dto.BookIssueDTO;
import com.library.dto.LibrarianDTO;
import com.library.service.LibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
@Tag(name = "Library Management", description = "APIs for Book Management and Issue/Return Operations")
public class LibraryController {

    private final LibraryService libraryService;

    @PostMapping("/librarian")
    @Operation(summary = "Create librarian profile", description = "Create librarian with 12-digit ID and attendance calendar")
    public ResponseEntity<LibrarianDTO> createLibrarian(@RequestBody LibrarianDTO librarianDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(libraryService.createLibrarian(librarianDTO));
    }

    @GetMapping("/librarian/{id}")
    @Operation(summary = "Get librarian profile", description = "Retrieve librarian details - READ ONLY")
    public ResponseEntity<LibrarianDTO> getLibrarian(@PathVariable Long id) {
        return ResponseEntity.ok(libraryService.getLibrarian(id));
    }

    @PostMapping("/books")
    @Operation(summary = "Add new book", description = "Add book with ID, name, author, publication, count")
    public ResponseEntity<BookDTO> addBook(@RequestBody BookDTO bookDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(libraryService.addBook(bookDTO));
    }

    @GetMapping("/books")
    @Operation(summary = "Get all books", description = "Retrieve book list with search and filter options")
    public ResponseEntity<List<BookDTO>> getAllBooks(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "false") boolean availableOnly) {
        return ResponseEntity.ok(libraryService.getAllBooks(searchTerm, availableOnly));
    }

    @GetMapping("/books/{bookId}")
    @Operation(summary = "Get book by ID", description = "Retrieve book details by book ID")
    public ResponseEntity<BookDTO> getBookById(@PathVariable String bookId) {
        return ResponseEntity.ok(libraryService.getBookById(bookId));
    }

    @PutMapping("/books/{bookId}")
    @Operation(summary = "Update book", description = "Update book count only (not book ID)")
    public ResponseEntity<BookDTO> updateBook(@PathVariable String bookId, @RequestBody BookDTO bookDTO) {
        return ResponseEntity.ok(libraryService.updateBook(bookId, bookDTO));
    }

    @DeleteMapping("/books/{bookId}")
    @Operation(summary = "Delete book", description = "Remove book from library system")
    public ResponseEntity<Map<String, String>> deleteBook(@PathVariable String bookId) {
        libraryService.deleteBook(bookId);
        return ResponseEntity.ok(Map.of("message", "Book deleted successfully"));
    }

    @PostMapping("/issue")
    @Operation(summary = "Issue book", description = "Issue book to student/faculty with issue date and returnable date")
    public ResponseEntity<BookIssueDTO> issueBook(@RequestBody BookIssueDTO bookIssueDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(libraryService.issueBook(bookIssueDTO));
    }

    @PostMapping("/return/{issueId}")
    @Operation(summary = "Return book", description = "Mark book as returned - updates count and records fine if overdue")
    public ResponseEntity<BookIssueDTO> returnBook(@PathVariable Long issueId) {
        return ResponseEntity.ok(libraryService.returnBook(issueId));
    }

    @GetMapping("/issue-records")
    @Operation(summary = "Get all issue records", description = "Retrieve issue/return log with filters")
    public ResponseEntity<List<BookIssueDTO>> getIssueRecords(
            @RequestParam(required = false) String userRole,
            @RequestParam(required = false) String userId) {
        return ResponseEntity.ok(libraryService.getIssueRecords(userRole, userId));
    }

    @GetMapping("/overdue-books")
    @Operation(summary = "Get overdue books", description = "Retrieve books that are overdue with days left and user details")
    public ResponseEntity<List<Map<String, Object>>> getOverdueBooks() {
        return ResponseEntity.ok(libraryService.getOverdueBooks());
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get library dashboard", description = "Complete library statistics and information")
    public ResponseEntity<Map<String, Object>> getLibraryDashboard() {
        return ResponseEntity.ok(libraryService.getLibraryDashboard());
    }

    @GetMapping("/total-books")
    @Operation(summary = "Get total books", description = "Get total count of all books in library")
    public ResponseEntity<Map<String, Integer>> getTotalBooksCount() {
        return ResponseEntity.ok(libraryService.getTotalBooksCount());
    }

    @GetMapping("/available-books")
    @Operation(summary = "Get available books", description = "Get count of currently available books")
    public ResponseEntity<Map<String, Integer>> getAvailableBooksCount() {
        return ResponseEntity.ok(libraryService.getAvailableBooksCount());
    }

    @GetMapping("/today-activity")
    @Operation(summary = "Get today's activity", description = "Get today's issue and return activities in chart format")
    public ResponseEntity<Map<String, Object>> getTodayActivity() {
        return ResponseEntity.ok(libraryService.getTodayActivity());
    }

    @GetMapping("/members-count")
    @Operation(summary = "Get total members", description = "Get total active users (excluding ADMIN) with role sorting")
    public ResponseEntity<Map<String, Object>> getTotalMembers() {
        return ResponseEntity.ok(libraryService.getTotalMembers());
    }
}