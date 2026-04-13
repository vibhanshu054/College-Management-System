package com.userService.services.impl;


import com.userService.client.FacultyServiceClient;
import com.userService.client.LibraryServiceClient;
import com.userService.client.StudentServiceClient;
import com.userService.dto.FacultyDTO;
import com.userService.dto.LibrarianDTO;
import com.userService.dto.StudentDTO;
import com.userService.entity.UserEntity;
import com.userService.enums.FacultySubRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoleServiceIntegration {

    private final StudentServiceClient studentServiceClient;
    private final FacultyServiceClient facultyServiceClient;
    private final LibraryServiceClient libraryServiceClient;

    /**
     * Create role-specific records in respective services
     * Called when a new user is created
     */
    public void createRoleSpecificRecord(UserEntity user) {
        log.info("Creating role-specific record for user: {} with role: {}", user.getEmail(), user.getRole());

        try {
            switch (user.getRole()) {
                case STUDENT:
                    createStudentRecord(user);
                    break;
                case FACULTY:
                    createFacultyRecord(user);
                    break;
                case LIBRARIAN:
                    createLibrarianRecord(user);
                    break;
                case ADMIN:
                    log.debug("No role-specific record needed for ADMIN");
                    break;
                default:
                    log.warn("Unknown role: {}", user.getRole());
            }
        } catch (Exception e) {
            log.error("Error creating role-specific record for user: {}", user.getEmail(), e);
            // Don't throw - let the user be created even if service is unavailable
            // Fallback pattern handles this
        }
    }

    /**
     * Delete role-specific records in respective services
     * Called when a user is deleted
     */
    public void deleteRoleSpecificRecord(UserEntity user) {
        log.info("Deleting role-specific record for user: {} with role: {}", user.getEmail(), user.getRole());

        try {
            switch (user.getRole()) {
                case STUDENT:
                    if (user.getStudentServiceId() != null) {
                        studentServiceClient.deleteStudent(user.getStudentServiceId());
                        log.debug("Student record deleted from Student-Service");
                    }
                    break;
                case FACULTY:
                    if (user.getFacultyServiceId() != null) {
                        facultyServiceClient.deleteFaculty(user.getFacultyServiceId());
                        log.debug("Faculty record deleted from Faculty-Service");
                    }
                    break;
                case LIBRARIAN:
                    if (user.getLibrarianServiceId() != null) {
                        libraryServiceClient.deleteLibrarian(user.getLibrarianServiceId());
                        log.debug("Librarian record deleted from Library-Service");
                    }
                    break;
                case ADMIN:
                    log.debug("No role-specific record to delete for ADMIN");
                    break;
                default:
                    log.warn("Unknown role: {}", user.getRole());
            }
        } catch (Exception e) {
            log.error("Error deleting role-specific record for user: {}", user.getEmail(), e);
            // Don't throw - let the user be deleted even if service is unavailable
        }
    }

    /**
     * Create student record in Student-Service
     */
    private void createStudentRecord(UserEntity user) {
        log.debug("Creating student record for user: {}", user.getEmail());

        StudentDTO studentDTO = StudentDTO.builder()
                .studentName(user.getUsername())
                .studentEmail(user.getEmail())
                .studentPhoneNumber(user.getPhoneNumber())
                .universityId(user.getUniversityId())
                .semester(user.getSemester())
                .batch(user.getBatch())
                .department(user.getDepartment())
                .course(user.getCourseCode())
                .courseCode(user.getCourseCode())
                .active(user.isActive())
                .build();

        try {
            var response = studentServiceClient.createStudentFromUser(studentDTO);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Student record created successfully for user: {}", user.getEmail());
            }
        } catch (Exception e) {
            log.warn("Failed to create student record, continuing with user creation", e);
        }
    }

    /**
     * Create faculty record in Faculty-Service
     */
    private void createFacultyRecord(UserEntity user) {
        log.debug("Creating faculty record for user: {}", user.getEmail());

        FacultyDTO facultyDTO = FacultyDTO.builder()
                .facultyName(user.getUsername())
                .facultyEmail(user.getEmail())
                .facultyPhoneNumber(user.getPhoneNumber())
                .universityId(user.getUniversityId())
                .department(user.getDepartment())
                .subRole(user.getFacultySubRole() != null ?
                        user.getFacultySubRole().toString() : FacultySubRole.TRAINEE.toString())
                .active(user.isActive())
                .build();

        try {
            var response = facultyServiceClient.createFacultyFromUser(facultyDTO);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Faculty record created successfully for user: {}", user.getEmail());
            }
        } catch (Exception e) {
            log.warn("Failed to create faculty record, continuing with user creation", e);
        }
    }

    /**
     * Create librarian record in Library-Service
     */
    private void createLibrarianRecord(UserEntity user) {
        log.debug("Creating librarian record for user: {}", user.getEmail());

        LibrarianDTO librarianDTO = LibrarianDTO.builder()
                .librarianName(user.getUsername())
                .librarianEmail(user.getEmail())
                .librarianPhoneNumber(user.getPhoneNumber())
                .universityId(user.getUniversityId())
                .active(user.isActive())
                .build();

        try {
            var response = libraryServiceClient.createLibrarianFromUser(librarianDTO);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Librarian record created successfully for user: {}", user.getEmail());
            }
        } catch (Exception e) {
            log.warn("Failed to create librarian record, continuing with user creation", e);
        }
    }
}