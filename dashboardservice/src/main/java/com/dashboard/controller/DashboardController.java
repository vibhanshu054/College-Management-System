package com.dashboard.controller;

import com.dashboard.dto.ApiResponse;
import com.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dashboard APIs", description = "Role-based dashboard endpoints")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    @Operation(summary = "Admin dashboard statistics")
    @SecurityRequirement(name = "Bearer Authentication") // FIXED: Consistent name
    public ResponseEntity<ApiResponse> getAdminDashboard(Authentication auth) {
        String username = getUsername(auth);
        log.info("Admin dashboard requested by: {}", username);
        return ResponseEntity.ok(dashboardService.getAdminDashboard());
    }

    @GetMapping("/student/{universityId}")
    @Operation(summary = "Student dashboard")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse> getStudentDashboard(
            @PathVariable String universityId,
            Authentication auth) {
        String username = getUsername(auth);
        log.info("Student dashboard requested by: {} for universityId: {}", username, universityId);
        return ResponseEntity.ok(dashboardService.getStudentDashboard(universityId));
    }

    @GetMapping("/faculty/{facultyUniversityId}")
    @Operation(summary = "Faculty dashboard")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse> getFacultyDashboard(
            @PathVariable String facultyUniversityId,
            Authentication auth) {
        String username = getUsername(auth);
        log.info("Faculty dashboard requested by: {} for facultyUniversityId: {}", username, facultyUniversityId);
        return ResponseEntity.ok(dashboardService.getFacultyDashboard(facultyUniversityId));
    }

    @GetMapping("/librarian")
    @Operation(summary = "Librarian dashboard")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse> getLibrarianDashboard(Authentication auth) {
        String username = getUsername(auth);
        log.info("Librarian dashboard requested by: {}", username);
        return ResponseEntity.ok(dashboardService.getLibrarianDashboard());
    }

    // Generic role-based (optional fallback)
    @GetMapping("/role/{role}/{userId}")
    @Operation(summary = "Generic role-based dashboard (use specific endpoints preferred)")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse> getDashboardByRole(
            @PathVariable String role,
            @PathVariable String userId,
            Authentication auth) {
        String username = getUsername(auth);
        log.info("Role-based dashboard requested by: {} for role: {} userId: {}", username, role, userId);
        return ResponseEntity.ok(dashboardService.getDashboardByRole(role, userId));
    }

    private String getUsername(Authentication auth) {
        return auth != null && auth.getName() != null ? auth.getName() : "ANONYMOUS";
    }
}