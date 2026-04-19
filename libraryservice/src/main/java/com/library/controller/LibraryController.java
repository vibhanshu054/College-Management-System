package com.library.controller;

import com.library.dto.ApiResponse;
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
    public ResponseEntity<ApiResponse> createLibrarian(@RequestBody LibrarianDTO librarianDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(libraryService.createLibrarian(librarianDTO));
    }

    @GetMapping("/librarian/{id}")
    public ResponseEntity<ApiResponse> getLibrarian(@PathVariable Long id) {
        return ResponseEntity.ok(libraryService.getLibrarian(id));
    }

    @GetMapping("/members")
    public ResponseEntity<ApiResponse> getLibraryMembers() {
        return ResponseEntity.ok(libraryService.getLibraryMembers());
    }

    @GetMapping("/members/count")
    public ResponseEntity<ApiResponse> getTotalMembers() {
        return ResponseEntity.ok(libraryService.getTotalMembers());
    }

    @GetMapping("/user/{universityId}/books")
    public ResponseEntity<ApiResponse> getUserBooks(@PathVariable String universityId) {
        return ResponseEntity.ok(libraryService.getBooksByUser(universityId));
    }

    @GetMapping("/user/{universityId}/books/count")
    public ResponseEntity<ApiResponse> getUserBookCount(@PathVariable String universityId) {
        return ResponseEntity.ok(libraryService.getBookCountByUser(universityId));
    }

    @PostMapping("/books")
    public ResponseEntity<ApiResponse> addBook(@RequestBody BookDTO bookDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(libraryService.addBook(bookDTO));
    }

    @GetMapping("/books")
    public ResponseEntity<ApiResponse> getAllBooks(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "false") boolean availableOnly) {
        return ResponseEntity.ok(libraryService.getAllBooks(searchTerm, availableOnly));
    }

    @GetMapping("/books/{bookId}")
    public ResponseEntity<ApiResponse> getBookById(@PathVariable String bookId) {
        return ResponseEntity.ok(libraryService.getBookById(bookId));
    }

    @PutMapping("/books/{bookId}")
    public ResponseEntity<ApiResponse> updateBook(@PathVariable String bookId,
                                                  @RequestBody BookDTO bookDTO) {
        return ResponseEntity.ok(libraryService.updateBook(bookId, bookDTO));
    }
    @GetMapping("/issue-records/all")
    public ResponseEntity<ApiResponse> getAllIssueRecords() {
        return ResponseEntity.ok(libraryService.getAllIssueRecords());
    }
    @GetMapping("/issue-records/role/{role}")
    public ResponseEntity<ApiResponse> getIssueRecordsByRole(
            @PathVariable String role,
            @RequestParam(required = false) String action) {
        return ResponseEntity.ok(libraryService.getIssueRecordsByRole(role, action));
    }
    @PutMapping("/books/{bookId}/quantity")
    public ResponseEntity<ApiResponse> updateBookQuantity(@PathVariable String bookId,
                                                          @RequestParam int quantityToAdd) {
        return ResponseEntity.ok(libraryService.updateBookQuantity(bookId, quantityToAdd));
    }

    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<ApiResponse> deleteBook(@PathVariable String bookId) {
        return ResponseEntity.ok(libraryService.deleteBook(bookId));
    }

    @PostMapping("/issue")
    public ResponseEntity<ApiResponse> issueBook(@RequestBody BookIssueDTO bookIssueDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(libraryService.issueBook(bookIssueDTO));
    }

    @PostMapping("/return/{issueId}")
    public ResponseEntity<ApiResponse> returnBook(@PathVariable Long issueId) {
        return ResponseEntity.ok(libraryService.returnBook(issueId));
    }

    @PutMapping("/issue/{issueId}/returnable-date")
    public ResponseEntity<ApiResponse> updateReturnableDate(@PathVariable Long issueId,
                                                            @RequestParam String returnableDate) {
        return ResponseEntity.ok(libraryService.updateReturnableDate(issueId, returnableDate));
    }

    @GetMapping("/issue-records")
    public ResponseEntity<ApiResponse> getIssueRecords(
            @RequestParam(required = false) String userRole,
            @RequestParam(required = false) String userId) {
        return ResponseEntity.ok(libraryService.getIssueRecords(userRole, userId));
    }

    @GetMapping("/overdue-books")
    public ResponseEntity<ApiResponse> getOverdueBooks() {
        return ResponseEntity.ok(libraryService.getOverdueBooks());
    }

    @GetMapping("/overdue-detailed")
    public ResponseEntity<ApiResponse> getOverdueDetailed() {
        return ResponseEntity.ok(libraryService.getOverdueBooksDetailed());
    }

    @GetMapping("/books/stock")
    public ResponseEntity<ApiResponse> getBookStock() {
        return ResponseEntity.ok(libraryService.getBookStock());
    }

    @GetMapping("/weekly-circulation")
    public ResponseEntity<ApiResponse> getWeeklyCirculation() {
        return ResponseEntity.ok(libraryService.getWeeklyCirculation());
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse> getLibraryDashboard() {
        return ResponseEntity.ok(libraryService.getLibraryDashboard());
    }

    @GetMapping("/total-books")
    public ResponseEntity<ApiResponse> getTotalBooksCount() {
        return ResponseEntity.ok(libraryService.getTotalBooksCount());
    }

    @GetMapping("/available-books")
    public ResponseEntity<ApiResponse> getAvailableBooksCount() {
        return ResponseEntity.ok(libraryService.getAvailableBooksCount());
    }

    @GetMapping("/today-activity")
    public ResponseEntity<ApiResponse> getTodayActivity() {
        return ResponseEntity.ok(libraryService.getTodayActivity());
    }
}