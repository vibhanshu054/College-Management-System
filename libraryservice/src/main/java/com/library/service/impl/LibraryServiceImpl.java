package com.library.service.impl;

import com.library.client.FacultyClient;
import com.library.client.StudentClient;
import com.library.client.UserServiceClient;
import com.library.dto.ApiResponse;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LibraryServiceImpl implements LibraryService {

    @Value("${library.issue.max-days:14}")
    private int maxDays;

    private final BookRepository bookRepository;
    private final BookIssueRepository bookIssueRepository;
    private final LibrarianRepository librarianRepository;
    private final StudentClient studentClient;
    private final UserServiceClient userClient;
    private final FacultyClient facultyClient;
    private final LibraryValidation libraryValidation;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse createLibrarian(LibrarianDTO librarianDTO) {
        log.info("Creating librarian: {}", librarianDTO.getLibrarianEmail());

        if (librarianDTO == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }

        librarianRepository.findByUniversityId(librarianDTO.getUniversityId())
                .ifPresent(l -> {
                    throw new IllegalArgumentException("Librarian already exists");
                });

        LibrarianEntity saved = librarianRepository.save(
                modelMapper.map(librarianDTO, LibrarianEntity.class)
        );

        return new ApiResponse(
                "Librarian created successfully",
                201,
                modelMapper.map(saved, LibrarianDTO.class),
                LocalDateTime.now()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse getLibrarian(Long id) {
        log.info("Fetching librarian {}", id);

        LibrarianEntity librarian = librarianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Librarian not found"));

        return new ApiResponse(
                "Librarian fetched",
                200,
                modelMapper.map(librarian, LibrarianDTO.class),
                LocalDateTime.now()
        );
    }

    @Override
    public ApiResponse addBook(BookDTO dto) {
        log.info("Adding book {}", dto.getBookName());

        if (dto == null || dto.getBookName() == null || dto.getBookName().isBlank()) {
            throw new IllegalArgumentException("Book name required");
        }

        if (dto.getTotalCount() <= 0) {
            throw new IllegalArgumentException("Total count must be greater than 0");
        }

        BookEntity entity = new BookEntity();
        entity.setBookId(generateBookCode(dto.getBookName()));
        entity.setBookName(dto.getBookName().trim());
        entity.setAuthor(dto.getAuthor());
        entity.setPublication(dto.getPublication());
        entity.setDescription(dto.getDescription());
        entity.setTotalCount(dto.getTotalCount());
        entity.setIssuedCount(0);
        entity.setAvailableCount(dto.getTotalCount());
        entity.setActive(true);

        BookEntity saved = bookRepository.save(entity);

        return new ApiResponse(
                "Book added",
                201,
                modelMapper.map(saved, BookDTO.class),
                LocalDateTime.now()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse getAllBooks(String searchTerm, boolean availableOnly) {
        List<BookEntity> books = bookRepository.findAll();

        if (searchTerm != null && !searchTerm.isBlank()) {
            String s = searchTerm.toLowerCase().trim();
            books = books.stream()
                    .filter(b -> b.getBookName() != null && b.getBookName().toLowerCase().contains(s))
                    .toList();
        }

        if (availableOnly) {
            books = books.stream()
                    .filter(b -> b.getAvailableCount() > 0)
                    .toList();
        }

        List<BookDTO> list = modelMapper.map(books, new TypeToken<List<BookDTO>>() {}.getType());

        return new ApiResponse("Books fetched", 200, list, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse getBooksByUser(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("UserId required");
        }

        List<BookIssueEntity> list = bookIssueRepository.findByUserId(userId.trim());

        return new ApiResponse(
                "User books",
                200,
                modelMapper.map(list, new TypeToken<List<BookIssueDTO>>() {}.getType()),
                LocalDateTime.now()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse getBookCountByUser(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("UserId required");
        }

        List<BookIssueEntity> list = bookIssueRepository.findByUserId(userId.trim());

        long issued = list.stream().filter(b -> b.getStatus() == IssueStatus.ISSUED).count();
        long returned = list.stream().filter(b -> b.getStatus() == IssueStatus.RETURNED).count();

        Map<String, Object> data = new HashMap<>();
        data.put("issued", issued);
        data.put("returned", returned);

        return new ApiResponse("Book count", 200, data, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse getBookById(String bookId) {
        BookEntity book = libraryValidation.validateBookExists(bookId);

        return new ApiResponse(
                "Book fetched",
                200,
                modelMapper.map(book, BookDTO.class),
                LocalDateTime.now()
        );
    }

    @Override
    public ApiResponse updateBook(String bookId, BookDTO dto) {
        BookEntity existing = libraryValidation.validateBookExists(bookId);

        if (dto.getBookName() != null && !dto.getBookName().isBlank()) {
            existing.setBookName(dto.getBookName().trim());
        }
        if (dto.getAuthor() != null) {
            existing.setAuthor(dto.getAuthor());
        }
        if (dto.getPublication() != null) {
            existing.setPublication(dto.getPublication());
        }
        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription());
        }

        BookEntity updated = bookRepository.save(existing);

        return new ApiResponse(
                "Book updated",
                200,
                modelMapper.map(updated, BookDTO.class),
                LocalDateTime.now()
        );
    }

    @Override
    public ApiResponse updateBookQuantity(String bookId, int quantityToAdd) {
        if (quantityToAdd == 0) {
            throw new IllegalArgumentException("Quantity change cannot be zero");
        }

        BookEntity book = libraryValidation.validateBookExists(bookId);

        int newTotal = book.getTotalCount() + quantityToAdd;
        if (newTotal < book.getIssuedCount()) {
            throw new IllegalArgumentException("Total count cannot be less than issued count");
        }
        if (newTotal < 0) {
            throw new IllegalArgumentException("Total count cannot be negative");
        }

        book.setTotalCount(newTotal);
        book.setAvailableCount(newTotal - book.getIssuedCount());

        BookEntity updated = bookRepository.save(book);

        return new ApiResponse(
                "Book quantity updated",
                200,
                modelMapper.map(updated, BookDTO.class),
                LocalDateTime.now()
        );
    }

    @Override
    public ApiResponse deleteBook(String bookId) {
        log.info("Deleting book {}", bookId);

        BookEntity existing = libraryValidation.validateBookExists(bookId);
        if (existing.getIssuedCount() > 0) {
            throw new IllegalArgumentException("Book currently issued, cannot delete");
        }

        bookRepository.delete(existing);

        return new ApiResponse("Book deleted", 200, null, LocalDateTime.now());
    }

    @Override
    public ApiResponse issueBook(BookIssueDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }
        if (dto.getUserId() == null || dto.getUserId().isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (dto.getBookId() == null || dto.getBookId().isBlank()) {
            throw new IllegalArgumentException("Book ID is required");
        }

        String userId = dto.getUserId().trim();
        String bookId = dto.getBookId().trim();

        long activeCount = bookIssueRepository.countByUserIdAndStatus(userId, IssueStatus.ISSUED);
        if (activeCount >= 6) {
            throw new IllegalArgumentException("Max 6 books allowed");
        }

        BookEntity book = libraryValidation.validateBookExists(bookId);

        if (book.getAvailableCount() <= 0) {
            throw new IllegalArgumentException("Book not available");
        }

        BookIssueEntity issue = new BookIssueEntity();
        issue.setBookId(book.getBookId());
        issue.setUserRole(dto.getUserRole());
        issue.setBookName(book.getBookName());
        issue.setUserId(userId);

        if (userId.startsWith("STU")) {
            ResponseEntity<ApiResponse> studentResponse = studentClient.getStudentByUniversityId(userId);

            if (studentResponse == null || studentResponse.getBody() == null || studentResponse.getBody().getData() == null) {
                throw new ResourceNotFoundException("Student not found for university id: " + userId);
            }

            Map<String, Object> studentData = (Map<String, Object>) studentResponse.getBody().getData();

            issue.setUserName(String.valueOf(studentData.get("studentName")));
            issue.setUserRole("STUDENT");
            issue.setUserEmail(String.valueOf(studentData.get("studentEmail")));
            issue.setDepartment((String) studentData.get("department"));
            issue.setCourse((String) studentData.get("course"));

        } else if (userId.startsWith("FAC")) {
            ResponseEntity<ApiResponse> facultyResponse = facultyClient.getFacultyByFacultyUniversityId(userId);

            if (facultyResponse == null || facultyResponse.getBody() == null || facultyResponse.getBody().getData() == null) {
                throw new ResourceNotFoundException("Faculty not found for university id: " + userId);
            }

            Map<String, Object> facultyData = (Map<String, Object>) facultyResponse.getBody().getData();

            issue.setUserName(String.valueOf(facultyData.get("facultyName")));
            issue.setUserRole("FACULTY");
            issue.setUserEmail(String.valueOf(facultyData.get("facultyEmail")));
            issue.setDepartment((String) facultyData.get("department"));
            issue.setCourse(null);

        } else {
            throw new IllegalArgumentException("Invalid userId format");
        }

        issue.setIssueDate(LocalDate.now());
        issue.setReturnableDate(LocalDate.now().plusDays(maxDays));
        issue.setStatus(IssueStatus.ISSUED);
        issue.setActiveIssue(true);
        issue.setReturnedDate(null);

        book.setIssuedCount(book.getIssuedCount() + 1);
        book.setAvailableCount(book.getAvailableCount() - 1);

        bookRepository.save(book);
        BookIssueEntity saved = bookIssueRepository.save(issue);

        return new ApiResponse("Book issued successfully", 201, saved, LocalDateTime.now());
    }

    @Override
    public ApiResponse returnBook(Long issueId) {
        BookIssueEntity issue = libraryValidation.validateIssueRecordExists(issueId);

        if (issue.getStatus() == IssueStatus.RETURNED) {
            throw new IllegalArgumentException("Book already returned");
        }

        BookEntity book = libraryValidation.validateBookExists(issue.getBookId());

        if (book.getIssuedCount() <= 0) {
            throw new IllegalArgumentException("Invalid return operation");
        }

        issue.setStatus(IssueStatus.RETURNED);
        issue.setReturnedDate(LocalDate.now());
        issue.setActiveIssue(false);

        book.setIssuedCount(book.getIssuedCount() - 1);
        book.setAvailableCount(book.getAvailableCount() + 1);

        bookRepository.save(book);
        BookIssueEntity updated = bookIssueRepository.save(issue);

        return new ApiResponse("Book returned successfully", 200, updated, LocalDateTime.now());
    }

    @Override
    public ApiResponse updateReturnableDate(Long issueId, String returnableDate) {
        BookIssueEntity issue = libraryValidation.validateIssueRecordExists(issueId);

        if (issue.getStatus() == IssueStatus.RETURNED) {
            throw new IllegalArgumentException("Cannot change returnable date after return");
        }

        LocalDate parsedDate = LocalDate.parse(returnableDate);
        if (parsedDate.isBefore(issue.getIssueDate())) {
            throw new IllegalArgumentException("Returnable date cannot be before issue date");
        }

        issue.setReturnableDate(parsedDate);
        BookIssueEntity updated = bookIssueRepository.save(issue);

        return new ApiResponse(
                "Returnable date updated successfully",
                200,
                updated,
                LocalDateTime.now()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse getBookStock() {
        log.info("Fetching book stock");

        List<BookEntity> books = bookRepository.findAll();

        List<Map<String, Object>> result = books.stream().map(book -> {
            Map<String, Object> map = new HashMap<>();
            map.put("bookId", book.getBookId());
            map.put("bookName", book.getBookName());
            map.put("total", book.getTotalCount());
            map.put("available", book.getAvailableCount());
            map.put("issued", book.getIssuedCount());
            return map;
        }).toList();

        return new ApiResponse("Book stock fetched", 200, result, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse getAvailableBooksCount() {

        List<BookEntity> books = bookRepository.findAll();

        int totalCopies = books.stream()
                .mapToInt(BookEntity::getTotalCount)
                .sum();

        int availableCopies = books.stream()
                .mapToInt(BookEntity::getAvailableCount)
                .sum();

        int issuedCopies = books.stream()
                .mapToInt(BookEntity::getIssuedCount)
                .sum();

        double issuedPercentage = totalCopies == 0
                ? 0.0
                : (issuedCopies * 100.0) / totalCopies;

        Map<String, Object> data = new HashMap<>();
        data.put("totalCopies", totalCopies);
        data.put("availableCopies", availableCopies);
        data.put("issuedCopies", issuedCopies);
        data.put("issuedPercentage", Math.round(issuedPercentage * 100.0) / 100.0);

        return new ApiResponse(
                "Book inventory summary fetched",
                200,
                data,
                LocalDateTime.now()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse getIssueRecords(String role, String userId) {
        List<BookIssueEntity> issues;

        boolean hasRole = role != null && !role.isBlank();
        boolean hasUserId = userId != null && !userId.isBlank();

        if (hasRole && hasUserId) {
            issues = bookIssueRepository.findByUserRoleAndUserId(role.trim(), userId.trim());
        } else if (hasRole) {
            issues = bookIssueRepository.findByUserRole(role.trim());
        } else if (hasUserId) {
            issues = bookIssueRepository.findByUserId(userId.trim());
        } else {
            issues = bookIssueRepository.findAll();
        }

        List<Map<String, Object>> result = issues.stream().map(issue -> {
            Map<String, Object> map = new HashMap<>();
            map.put("issueId", issue.getId());
            map.put("bookId", issue.getBookId());
            map.put("bookName", issue.getBookName());
            map.put("userUniversityId", issue.getUserId());
            map.put("userName", issue.getUserName());
            map.put("userEmail", issue.getUserEmail());
            map.put("department", issue.getDepartment());
            map.put("course", issue.getCourse());
            map.put("role", issue.getUserRole());
            map.put("issueDate", issue.getIssueDate());
            map.put("returnableDate", issue.getReturnableDate());
            map.put("returnedDate", issue.getReturnedDate());
            map.put("status", issue.getStatus());
            map.put("actionIssued", issue.getActiveIssue());
            return map;
        }).toList();

        return new ApiResponse("Issue records fetched", 200, result, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse getLibraryMembers() {
        List<BookIssueEntity> issues = bookIssueRepository.findAll();

        Map<String, Map<String, Object>> uniqueMembers = new HashMap<>();

        for (BookIssueEntity issue : issues) {
            if (!uniqueMembers.containsKey(issue.getUserId())) {
                Map<String, Object> member = new HashMap<>();
                member.put("userId", issue.getUserId());
                member.put("userName", issue.getUserName());
                member.put("email", issue.getUserEmail());
                member.put("department", issue.getDepartment());
                member.put("course", issue.getCourse());
                member.put("role", issue.getUserRole());

                uniqueMembers.put(issue.getUserId(), member);
            }
        }

        return new ApiResponse(
                "Library associated members fetched",
                200,
                new ArrayList<>(uniqueMembers.values()),
                LocalDateTime.now()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse getTotalMembers() {
        long totalMembers = bookIssueRepository.countDistinctUserId();

        return new ApiResponse(
                "Library associated members count fetched",
                200,
                Map.of("totalMembers", totalMembers),
                LocalDateTime.now()
        );
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse getTotalMembersByUserId() {
        try {
            List<Map<String, Object>> users = userClient.getAllUsers();

            long students = users.stream()
                    .filter(u -> "STUDENT".equalsIgnoreCase(String.valueOf(u.get("role"))))
                    .count();

            long faculty = users.stream()
                    .filter(u -> "FACULTY".equalsIgnoreCase(String.valueOf(u.get("role"))))
                    .count();

            long admin = users.stream()
                    .filter(u -> "ADMIN".equalsIgnoreCase(String.valueOf(u.get("role"))))
                    .count();

            Map<String, Object> data = new HashMap<>();
            data.put("totalUsers", users.size());
            data.put("students", students);
            data.put("faculty", faculty);
            data.put("admin", admin);

            return new ApiResponse("Members", 200, data, LocalDateTime.now());
        } catch (Exception e) {
            log.error("User service down", e);
            return new ApiResponse("User service down", 500, null, LocalDateTime.now());
        }
    }
    @Transactional(readOnly = true)
    @Override
    public ApiResponse getAllIssueRecords() {

        List<BookIssueEntity> issues = bookIssueRepository.findAll();

        List<Map<String, Object>> result = issues.stream().map(issue -> {
            Map<String, Object> map = new HashMap<>();
            map.put("issueId", issue.getId());
            map.put("bookId", issue.getBookId());
            map.put("bookName", issue.getBookName());
            map.put("userUniversityId", issue.getUserId());
            map.put("userName", issue.getUserName());
            map.put("userEmail", issue.getUserEmail());
            map.put("department", issue.getDepartment());
            map.put("course", issue.getCourse());
            map.put("role", issue.getUserRole());
            map.put("issueDate", issue.getIssueDate());
            map.put("returnableDate", issue.getReturnableDate());
            map.put("returnedDate", issue.getReturnedDate());
            map.put("status", issue.getStatus());
            map.put("actionIssued", issue.getActiveIssue());
            return map;
        }).toList();

        return new ApiResponse(
                "All issue records fetched successfully",
                200,
                result,
                LocalDateTime.now()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse getOverdueBooks() {
        List<BookIssueEntity> list =
                bookIssueRepository.findByReturnedDateIsNullAndReturnableDateBefore(LocalDate.now());

        List<Map<String, Object>> result = list.stream().map(i -> {
            Map<String, Object> m = new HashMap<>();
            m.put("bookId", i.getBookId());
            m.put("bookName", i.getBookName());
            m.put("userId", i.getUserId());
            m.put("userName", i.getUserName());
            m.put("daysOverdue", ChronoUnit.DAYS.between(i.getReturnableDate(), LocalDate.now()));
            return m;
        }).toList();

        return new ApiResponse("Overdue", 200, result, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse getOverdueBooksDetailed() {
        log.info("Fetching overdue detailed");

        List<BookIssueEntity> list =
                bookIssueRepository.findByReturnedDateIsNullAndReturnableDateBefore(LocalDate.now());

        List<Map<String, Object>> result = list.stream().map(issue -> {
            Map<String, Object> map = new HashMap<>();
            map.put("issueId", issue.getId());
            map.put("userId", issue.getUserId());
            map.put("userRole", issue.getUserRole());
            map.put("userName", issue.getUserName());
            map.put("userEmail", issue.getUserEmail());
            map.put("department", issue.getDepartment());
            map.put("course", issue.getCourse());
            map.put("bookId", issue.getBookId());
            map.put("bookName", issue.getBookName());
            map.put("issueDate", issue.getIssueDate());
            map.put("returnableDate", issue.getReturnableDate());
            map.put("daysOverdue", ChronoUnit.DAYS.between(issue.getReturnableDate(), LocalDate.now()));
            return map;
        }).toList();

        return new ApiResponse("Overdue detailed", 200, result, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse getWeeklyCirculation() {
        log.info("Fetching weekly circulation");

        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(6);

        List<Map<String, Object>> result = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate date = start.plusDays(i);

            long issued = bookIssueRepository.countByIssueDate(date);
            long returned = bookIssueRepository.countByReturnedDate(date);

            Map<String, Object> map = new HashMap<>();
            map.put("day", date.getDayOfWeek().toString());
            map.put("date", date);
            map.put("issued", issued);
            map.put("returned", returned);

            result.add(map);
        }

        return new ApiResponse("Weekly circulation fetched", 200, result, LocalDateTime.now());
    }
    @Override
    @Transactional(readOnly = true)
    public ApiResponse getIssueRecordsByRole(String role, String action) {

        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role is required");
        }

        List<BookIssueEntity> issues = bookIssueRepository.findByUserRoleIgnoreCase(role.trim());

        if (action != null && !action.isBlank()) {
            String normalizedAction = action.trim().toUpperCase();

            if ("ISSUED".equals(normalizedAction)) {
                issues = issues.stream()
                        .filter(i -> i.getStatus() == IssueStatus.ISSUED)
                        .toList();
            } else if ("RETURNED".equals(normalizedAction)) {
                issues = issues.stream()
                        .filter(i -> i.getStatus() == IssueStatus.RETURNED)
                        .toList();
            } else {
                throw new IllegalArgumentException("Action must be ISSUED or RETURNED");
            }
        }

        List<Map<String, Object>> result = issues.stream().map(issue -> {
            Map<String, Object> map = new HashMap<>();
            map.put("issueId", issue.getId());
            map.put("userId", issue.getUserId());
            map.put("userName", issue.getUserName());
            map.put("role", issue.getUserRole());
            map.put("bookId", issue.getBookId());
            map.put("bookName", issue.getBookName());
            map.put("issueDate", issue.getIssueDate());
            map.put("returnableDate", issue.getReturnableDate());
            map.put("returnedDate", issue.getReturnedDate());
            map.put("status", issue.getStatus());
            map.put("activeIssue", issue.getActiveIssue());
            return map;
        }).toList();

        return new ApiResponse("Role wise issue records fetched", 200, result, LocalDateTime.now());
    }
    @Override
    @Transactional(readOnly = true)
    public ApiResponse getTodayActivity() {

        LocalDate today = LocalDate.now();

        List<BookIssueEntity> records = bookIssueRepository.findByIssueDateOrReturnedDate(today, today);

        List<Map<String, Object>> activityList = new ArrayList<>();

        for (BookIssueEntity record : records) {

            if (record.getIssueDate() != null && record.getIssueDate().equals(today)) {
                activityList.add(buildTodayActivityRow(record, "ISSUED", record.getIssueDate()));
            }

            if (record.getReturnedDate() != null && record.getReturnedDate().equals(today)) {
                activityList.add(buildTodayActivityRow(record, "RETURNED", record.getReturnedDate()));
            }
        }

        return new ApiResponse(
                "Today's activity fetched",
                200,
                activityList,
                LocalDateTime.now()
        );
    }

    private Map<String, Object> buildTodayActivityRow(BookIssueEntity record, String activityType, LocalDate activityDate) {
        Map<String, Object> row = new HashMap<>();
        row.put("issueId", record.getId());
        row.put("bookId", record.getBookId());
        row.put("bookName", record.getBookName());
        row.put("userId", record.getUserId());
        row.put("userName", record.getUserName());
        row.put("userRole", record.getUserRole());
        row.put("userEmail", record.getUserEmail());
        row.put("department", record.getDepartment());
        row.put("course", record.getCourse());
        row.put("activityType", activityType);
        row.put("activityDate", activityDate);
        row.put("status", "ISSUED".equals(activityType) ? IssueStatus.ISSUED : IssueStatus.RETURNED);
        return row;
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse getLibraryDashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalBookTitles", bookRepository.count());
        data.put("availableCopies", bookRepository.findAll().stream().mapToInt(BookEntity::getAvailableCount).sum());
        data.put("issuedCopies", bookRepository.findAll().stream().mapToInt(BookEntity::getIssuedCount).sum());
        data.put("libraryMembers", bookIssueRepository.countDistinctUserId());

        return new ApiResponse("Dashboard", 200, data, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse getTotalBooksCount() {
        return new ApiResponse(
                "Total books",
                200,
                Map.of("total", bookRepository.count()),
                LocalDateTime.now()
        );
    }

    private String generateBookCode(String bookName) {
        String cleaned = bookName == null ? "" : bookName.trim();
        String prefix = cleaned.length() >= 3
                ? cleaned.substring(0, 3).toUpperCase()
                : cleaned.toUpperCase();

        long count = bookRepository.count() + 1;
        return prefix + "-" + String.format("%04d", count);
    }
}