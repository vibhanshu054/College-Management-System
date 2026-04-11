package com.collage.dashboard.controller;


import com.collage.dashboard.dto.*;
import com.collage.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dashboard", description = "APIs for Role-Based Dashboards")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    @Operation(summary = "Get admin dashboard", description = "Retrieve admin dashboard with statistics")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<AdminDashboardDTO> getAdminDashboard(Authentication auth) {
        log.info("Admin dashboard request from: {}", auth.getName());
        return ResponseEntity.ok(dashboardService.getAdminDashboard());
    }

    @GetMapping("/faculty/{facultyId}")
    @Operation(summary = "Get faculty dashboard", description = "Retrieve faculty dashboard")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<FacultyDashboardDTO> getFacultyDashboard(
            @PathVariable String facultyId,
            Authentication auth) {
        log.info("Faculty dashboard request from: {}", auth.getName());
        return ResponseEntity.ok(dashboardService.getFacultyDashboard(facultyId));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get student dashboard", description = "Retrieve student dashboard")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<StudentDashboardDTO> getStudentDashboard(
            @PathVariable String studentId,
            Authentication auth) {
        log.info("Student dashboard request from: {}", auth.getName());
        return ResponseEntity.ok(dashboardService.getStudentDashboard(studentId));
    }

    @GetMapping("/librarian")
    @Operation(summary = "Get librarian dashboard", description = "Retrieve librarian dashboard")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<LibrarianDashboardDTO> getLibrarianDashboard(Authentication auth) {
        log.info("Librarian dashboard request from: {}", auth.getName());
        return ResponseEntity.ok(dashboardService.getLibrarianDashboard());
    }

    @GetMapping("/{role}/{userId}")
    @Operation(summary = "Get dashboard by role", description = "Retrieve role-based dashboard")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<DashboardDTO> getDashboardByRole(
            @PathVariable String role,
            @PathVariable String userId,
            Authentication auth) {
        log.info("Dashboard request for role: {} from: {}", role, auth.getName());
        return ResponseEntity.ok(dashboardService.getDashboardByRole(role, userId));
    }
}