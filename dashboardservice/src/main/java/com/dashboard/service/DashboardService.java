package com.collage.dashboard.service;


import com.collage.dashboard.clients.*;
import com.collage.dashboard.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {

    private final UserServiceClient userServiceClient;
    private final StudentServiceClient studentServiceClient;
    private final FacultyServiceClient facultyServiceClient;
    private final CourseServiceClient courseServiceClient;
    private final DepartmentServiceClient departmentServiceClient;
    private final LibraryServiceClient libraryServiceClient;

    /**
     * Get admin dashboard data
     */
    public AdminDashboardDTO getAdminDashboard() {
        log.info("Fetching admin dashboard data");

        try {
            long totalStudents = studentServiceClient.getTotalStudentsCount().getOrDefault("totalStudents", 0L);
            long totalFaculty = facultyServiceClient.getTotalFacultyCount().getOrDefault("totalFaculty", 0L);
            long totalDepartments = departmentServiceClient.getTotalDepartmentsCount().getOrDefault("totalDepartments", 0L);
            long totalCourses = courseServiceClient.getTotalCoursesCount().getOrDefault("totalCourses", 0L);

            Map<String, Long> userDistribution = new HashMap<>();
            userDistribution.put("students", totalStudents);
            userDistribution.put("faculty", totalFaculty);

            return AdminDashboardDTO.builder()
                    .totalStudents(totalStudents)
                    .totalFaculty(totalFaculty)
                    .totalDepartments(totalDepartments)
                    .totalCourses(totalCourses)
                    .userDistribution(userDistribution)
                    .build();
        } catch (Exception e) {
            log.error("Error fetching admin dashboard: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch admin dashboard data");
        }
    }

    /**
     * Get faculty dashboard data
     */
    public FacultyDashboardDTO getFacultyDashboard(String facultyId) {
        log.info("Fetching faculty dashboard data for: {}", facultyId);

        try {
            return FacultyDashboardDTO.builder()
                    .facultyName(facultyId)
                    .totalClassesAssigned(0)
                    .totalStudents(0)
                    .attendancePercentage(0.0)
                    .build();
        } catch (Exception e) {
            log.error("Error fetching faculty dashboard: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch faculty dashboard data");
        }
    }

    /**
     * Get student dashboard data
     */
    public StudentDashboardDTO getStudentDashboard(String studentId) {
        log.info("Fetching student dashboard data for: {}", studentId);

        try {
            return StudentDashboardDTO.builder()
                    .studentName(studentId)
                    .courseName("N/A")
                    .attendancePercentage(0.0)
                    .booksIssued(0)
                    .booksReturned(0)
                    .build();
        } catch (Exception e) {
            log.error("Error fetching student dashboard: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch student dashboard data");
        }
    }

    /**
     * Get librarian dashboard data
     */
    public LibrarianDashboardDTO getLibrarianDashboard() {
        log.info("Fetching librarian dashboard data");

        try {
            Map<String, Object> libraryData = libraryServiceClient.getLibraryDashboard();

            return LibrarianDashboardDTO.builder()
                    .totalBooks((Integer) libraryData.getOrDefault("totalBooks", 0))
                    .availableBooks((Integer) libraryData.getOrDefault("availableBooks", 0))
                    .issuedBooks((Integer) libraryData.getOrDefault("issuedBooks", 0))
                    .build();
        } catch (Exception e) {
            log.error("Error fetching librarian dashboard: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch librarian dashboard data");
        }
    }

    /**
     * Get complete dashboard based on role
     */
    public DashboardDTO getDashboardByRole(String role, String userId) {
        log.info("Fetching dashboard for role: {} and user: {}", role, userId);

        DashboardDTO dashboard = new DashboardDTO();
        dashboard.setLastUpdated(LocalDateTime.now());

        try {
            switch (role.toUpperCase()) {
                case "ADMIN":
                    dashboard.setAdminDashboard(getAdminDashboard());
                    break;
                case "FACULTY":
                    dashboard.setFacultyDashboard(getFacultyDashboard(userId));
                    break;
                case "STUDENT":
                    dashboard.setStudentDashboard(getStudentDashboard(userId));
                    break;
                case "LIBRARIAN":
                    dashboard.setLibrarianDashboard(getLibrarianDashboard());
                    break;
                default:
                    log.warn("Unknown role: {}", role);
            }
        } catch (Exception e) {
            log.error("Error fetching dashboard for role {}: {}", role, e.getMessage());
        }

        return dashboard;
    }
}