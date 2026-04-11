package com.collage.dashboard.controller;

import com.collage.dashboard.service.AdminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/dashboard/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Dashboard", description = "APIs for Admin Dashboard - complete system overview")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping
    @Operation(summary = "Get admin dashboard", description = "Complete admin dashboard with all statistics")
    public ResponseEntity<Map<String, Object>> getAdminDashboard() {
        return ResponseEntity.ok(adminDashboardService.getAdminDashboard());
    }

    @GetMapping("/courses")
    @Operation(summary = "Get all courses", description = "Total courses list with course ID")
    public ResponseEntity<Map<String, Object>> getTotalCourses() {
        return ResponseEntity.ok(adminDashboardService.getTotalCourses());
    }

    @GetMapping("/students")
    @Operation(summary = "Get all students", description = "All students list filtered by course, semester, department")
    public ResponseEntity<Map<String, Object>> getTotalStudents(
            @RequestParam(required = false) String course,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String department) {
        return ResponseEntity.ok(adminDashboardService.getTotalStudents(course, semester, department));
    }

    @GetMapping("/user-graph")
    @Operation(summary = "Get user distribution graph", description = "Graph showing admin, faculty, librarian, student counts")
    public ResponseEntity<Map<String, Object>> getUserDistributionGraph() {
        return ResponseEntity.ok(adminDashboardService.getUserDistributionGraph());
    }

    @GetMapping("/courses-graph")
    @Operation(summary = "Get courses distribution graph", description = "Donut chart with courses wise student count")
    public ResponseEntity<Map<String, Object>> getCoursesDistributionGraph() {
        return ResponseEntity.ok(adminDashboardService.getCoursesDistributionGraph());
    }

    @GetMapping("/departments")
    @Operation(summary = "Get all departments", description = "Department wise faculty and student count")
    public ResponseEntity<Map<String, Object>> getTotalDepartments() {
        return ResponseEntity.ok(adminDashboardService.getTotalDepartments());
    }
}