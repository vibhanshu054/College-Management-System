package com.college.attendance_Service.controller;



import com.college.attendance_Service.dto.AttendanceDTO;
import com.college.attendance_Service.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Attendance Management", description = "APIs for Attendance Management")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    @Operation(summary = "Mark attendance", description = "Faculty - Mark attendance for students")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<AttendanceDTO> markAttendance(
            @Valid @RequestBody AttendanceDTO attendanceDTO,
            Authentication auth) {
        log.info("Mark attendance request from: {}", auth.getName());
        AttendanceDTO marked = attendanceService.markAttendance(attendanceDTO, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(marked);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get attendance record", description = "Retrieve attendance record by ID")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<AttendanceDTO> getAttendance(@PathVariable Long id) {
        log.info("Get attendance {} request", id);
        return ResponseEntity.ok(attendanceService.getAttendanceById(id));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get student attendance", description = "Get all attendance records for a student")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<AttendanceDTO>> getStudentAttendance(@PathVariable String studentId) {
        log.info("Get student {} attendance request", studentId);
        return ResponseEntity.ok(attendanceService.getStudentAttendance(studentId));
    }

    @GetMapping("/course/{courseId}/date/{date}")
    @Operation(summary = "Get course attendance for date", description = "Get attendance for all students in a course on specific date")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<AttendanceDTO>> getCourseAttendanceForDate(
            @PathVariable String courseId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Get course {} attendance for date {}", courseId, date);
        return ResponseEntity.ok(attendanceService.getCourseAttendanceForDate(courseId, date));
    }

    @GetMapping("/faculty/{facultyId}/range")
    @Operation(summary = "Get faculty attendance records", description = "Get attendance records marked by faculty in date range")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<AttendanceDTO>> getFacultyAttendanceRecords(
            @PathVariable String facultyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Get faculty {} attendance records from {} to {}", facultyId, startDate, endDate);
        return ResponseEntity.ok(attendanceService.getFacultyAttendanceRecords(facultyId, startDate, endDate));
    }

    @GetMapping("/percentage/{studentId}/range")
    @Operation(summary = "Get attendance percentage", description = "Calculate attendance percentage for student in date range")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Double>> getAttendancePercentage(
            @PathVariable String studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Get attendance percentage for student {}", studentId);
        double percentage = attendanceService.calculateAttendancePercentage(studentId, startDate, endDate);
        return ResponseEntity.ok(Map.of("attendancePercentage", percentage));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update attendance", description = "Update attendance record")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<AttendanceDTO> updateAttendance(
            @PathVariable Long id,
            @Valid @RequestBody AttendanceDTO attendanceDTO,
            Authentication auth) {
        log.info("Update attendance {} request from: {}", id, auth.getName());
        AttendanceDTO updated = attendanceService.updateAttendance(id, attendanceDTO, auth.getName());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete attendance", description = "Delete attendance record")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, String>> deleteAttendance(
            @PathVariable Long id,
            Authentication auth) {
        log.info("Delete attendance {} request from: {}", id, auth.getName());
        attendanceService.deleteAttendance(id, auth.getName());
        return ResponseEntity.ok(Map.of("message", "Attendance record deleted successfully"));
    }
}