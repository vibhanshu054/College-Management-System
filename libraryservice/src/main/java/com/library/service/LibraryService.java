package com.library.service;


import com.library.dto.BookDTO;
import com.library.dto.BookIssueDTO;
import com.library.dto.LibrarianDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface LibraryService {


    LibrarianDTO createLibrarian(LibrarianDTO librarianDTO);

    LibrarianDTO getLibrarian(Long id);

    BookDTO addBook(BookDTO bookDTO);

    List<BookDTO> getAllBooks(String searchTerm, boolean availableOnly);

    BookDTO getBookById(String bookId);

    BookDTO updateBook(String bookId, BookDTO bookDTO);

    void deleteBook(String bookId);

    BookIssueDTO issueBook(BookIssueDTO bookIssueDTO);

    BookIssueDTO returnBook(Long issueId);

    List<BookIssueDTO> getIssueRecords(String userRole, String userId);

    List<Map<String, Object>> getOverdueBooks();

    Map<String, Object> getLibraryDashboard();

    Map<String, Integer> getTotalBooksCount();

    Map<String, Integer> getAvailableBooksCount();

    Map<String, Object> getTodayActivity();

    Map<String, Object> getTotalMembers();
}