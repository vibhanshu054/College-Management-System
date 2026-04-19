package com.library.service;

import com.library.dto.ApiResponse;
import com.library.dto.BookDTO;
import com.library.dto.BookIssueDTO;
import com.library.dto.LibrarianDTO;
import org.springframework.transaction.annotation.Transactional;

public interface LibraryService {

    // ================= LIBRARIAN =================
    ApiResponse createLibrarian(LibrarianDTO librarianDTO);
    ApiResponse getLibrarian(Long id);

    // ================= BOOK =================
    ApiResponse addBook(BookDTO bookDTO);
    ApiResponse getAllBooks(String searchTerm, boolean availableOnly);
    ApiResponse getBookById(String bookId);
    ApiResponse updateBook(String bookId, BookDTO bookDTO);

    ApiResponse updateBookQuantity(String bookId, int quantityToAdd);

    ApiResponse deleteBook(String bookId);

    // ================= BOOK ISSUE =================
    ApiResponse issueBook(BookIssueDTO bookIssueDTO);
    ApiResponse returnBook(Long issueId);

    ApiResponse getBooksByUser(String userId);
    ApiResponse getBookCountByUser(String userId);

    ApiResponse getIssueRecords(String userRole, String userId);

    @Transactional(readOnly = true)
    ApiResponse getTotalMembersByUserId();

    @Transactional(readOnly = true)
    ApiResponse getAllIssueRecords();

    // ================= ANALYTICS =================
    ApiResponse getOverdueBooks();
    ApiResponse getLibraryDashboard();
    ApiResponse getTotalBooksCount();
    ApiResponse getAvailableBooksCount();
    ApiResponse getTodayActivity();
    ApiResponse getTotalMembers();

    // ================= ADVANCED =================
    ApiResponse getOverdueBooksDetailed();
    ApiResponse getBookStock();
    ApiResponse getWeeklyCirculation();

    ApiResponse getLibraryMembers();


    ApiResponse updateReturnableDate(Long issueId, String returnableDate);

    ApiResponse getIssueRecordsByRole(String role, String action);
}