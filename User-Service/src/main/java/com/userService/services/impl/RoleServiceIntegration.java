package com.userService.services.impl;


import com.userService.dto.FacultyDTO;
import com.userService.dto.LibrarianDTO;
import com.userService.dto.StudentDto;
import com.userService.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceIntegration {

    private final StudentServiceClient studentServiceClient;
    private final FacultyServiceClient facultyServiceClient;
    private final LibraryServiceClient libraryServiceClient;

    public Long createStudentInStudentService(UserEntity user) {
        log.info("Creating student in Student-Service for user: {}", user.getEmail());
        try {
            return studentServiceClient.createStudentFromUser(convertToStudentDto(user));
        } catch (Exception e) {
            log.error("Error creating student in Student-Service: {}", e.getMessage());
            throw new RuntimeException("Failed to create student in Student-Service", e);
        }
    }

    public Long createFacultyInFacultyService(UserEntity user) {
        log.info("Creating faculty in Faculty-Service for user: {}", user.getEmail());
        try {
            return facultyServiceClient.createFacultyFromUser(convertToFacultyDTO(user));
        } catch (Exception e) {
            log.error("Error creating faculty in Faculty-Service: {}", e.getMessage());
            throw new RuntimeException("Failed to create faculty in Faculty-Service", e);
        }
    }

    public Long createLibrarianInLibraryService(UserEntity user) {
        log.info("Creating librarian in Library-Service for user: {}", user.getEmail());
        try {
            return libraryServiceClient.createLibrarianFromUser(convertToLibrarianDTO(user));
        } catch (Exception e) {
            log.error("Error creating librarian in Library-Service: {}", e.getMessage());
            throw new RuntimeException("Failed to create librarian in Library-Service", e);
        }
    }

    public void deleteStudentFromStudentService(Long studentServiceId) {
        log.info("Deleting student from Student-Service with ID: {}", studentServiceId);
        try {
            studentServiceClient.deleteStudent(studentServiceId);
        } catch (Exception e) {
            log.error("Error deleting student from Student-Service: {}", e.getMessage());
        }
    }

    public void deleteFacultyFromFacultyService(Long facultyServiceId) {
        log.info("Deleting faculty from Faculty-Service with ID: {}", facultyServiceId);
        try {
            facultyServiceClient.deleteFaculty(facultyServiceId);
        } catch (Exception e) {
            log.error("Error deleting faculty from Faculty-Service: {}", e.getMessage());
        }
    }

    public void deleteLibrarianFromLibraryService(Long librarianServiceId) {
        log.info("Deleting librarian from Library-Service with ID: {}", librarianServiceId);
        try {
            libraryServiceClient.deleteLibrarian(librarianServiceId);
        } catch (Exception e) {
            log.error("Error deleting librarian from Library-Service: {}", e.getMessage());
        }
    }

    private StudentDto convertToStudentDto(UserEntity user) {
        return StudentDto.builder()
                .universityId(user.getUniversityId())
                .studentName(user.getUsername())
                .studentEmail(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .semester(user.getSemester())
                .batch(user.getBatch())
                .department(user.getDepartment())
                .courseCode(user.getCourseCode())
                .build();
    }

    private FacultyDTO convertToFacultyDTO(UserEntity user) {
        return FacultyDTO.builder()
                .universityId(user.getUniversityId())
                .facultyName(user.getUsername())
                .facultyEmail(user.getEmail())
                .facultyPhoneNumber(user.getPhoneNumber())
                .department(user.getDepartment())
                .subRole(user.getFacultySubRole() != null ? user.getFacultySubRole().toString() : "TRAINEE")
                .build();
    }

    private LibrarianDTO convertToLibrarianDTO(UserEntity user) {
        return LibrarianDTO.builder()
                .universityId(user.getUniversityId())
                .librarianName(user.getUsername())
                .librarianEmail(user.getEmail())
                .librarianPhoneNumber(user.getPhoneNumber())
                .build();
    }
}

// Feign Clients
@FeignClient(name = "student-service")
interface StudentServiceClient {
    @PostMapping("/students/internal/create")
    Long createStudentFromUser(@RequestBody StudentDto StudentDto);

    @DeleteMapping("/students/{id}")
    void deleteStudent(@PathVariable Long id);
}

@FeignClient(name = "faculty-service")
interface FacultyServiceClient {
    @PostMapping("/faculty/internal/create")
    Long createFacultyFromUser(@RequestBody FacultyDTO facultyDTO);

    @DeleteMapping("/faculty/{id}")
    void deleteFaculty(@PathVariable Long id);
}

@FeignClient(name = "libraryservice")
interface LibraryServiceClient {
    @PostMapping("/library/librarian/internal/create")
    Long createLibrarianFromUser(@RequestBody LibrarianDTO librarianDTO);

    @DeleteMapping("/library/librarian/{id}")
    void deleteLibrarian(@PathVariable Long id);
}