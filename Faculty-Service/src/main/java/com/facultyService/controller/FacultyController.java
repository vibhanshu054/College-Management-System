package com.facultyService.controller;



import com.facultyService.dto.FacultyDTO;
import com.facultyService.service.FacultyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/faculty")
@RequiredArgsConstructor
@Tag(name = "Faculty Management", description = "APIs for Faculty Profile and Management")
public class FacultyController {

    private final FacultyService facultyService;

    // ============ PROFILE ENDPOINTS ============

    @PostMapping
    @Operation(summary = "Create new faculty", description = "Create faculty profile with all required fields")
    public ResponseEntity<FacultyDTO> createFaculty(@RequestBody FacultyDTO facultyDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(facultyService.createFaculty(facultyDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get faculty by ID", description = "Retrieve faculty profile - READ ONLY")
    public ResponseEntity<FacultyDTO> getFaculty(@PathVariable Long id) {
        return ResponseEntity.ok(facultyService.getFaculty(id));
    }

    @GetMapping("/university-id/{universityId}")
    @Operation(summary = "Get faculty by University ID", description = "Retrieve faculty profile using 12-digit university ID")
    public ResponseEntity<FacultyDTO> getFacultyByUniversityId(@PathVariable String universityId) {
        return ResponseEntity.ok(facultyService.getFacultyByUniversityId(universityId));
    }

    @GetMapping
    @Operation(summary = "Get all faculty", description = "Retrieve all faculty with filters")
    public ResponseEntity<List<FacultyDTO>> getAllFaculty(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String subRole) {
        return ResponseEntity.ok(facultyService.getAllFaculty(department, subRole));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update faculty profile", description = "Update faculty information (not university ID)")
    public ResponseEntity<FacultyDTO> updateFaculty(@PathVariable Long id, @RequestBody FacultyDTO facultyDTO) {
        return ResponseEntity.ok(facultyService.updateFaculty(id, facultyDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete faculty", description = "Remove faculty from system (cascade operations)")
    public ResponseEntity<Map<String, String>> deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.ok(Map.of("message", "Faculty deleted successfully"));
    }

    // ============ DASHBOARD ENDPOINTS ============

    @GetMapping("/{id}/dashboard")
    @Operation(summary = "Get faculty dashboard", description = "Get profile, books issued/returned, today's schedule")
    public ResponseEntity<Map<String, Object>> getFacultyDashboard(@PathVariable Long id) {
        return ResponseEntity.ok(facultyService.getFacultyDashboard(id));
    }

    @GetMapping("/{id}/attendance")
    @Operation(summary = "Get faculty attendance calendar", description = "Retrieve attendance record - READ ONLY")
    public ResponseEntity<String> getAttendanceCalendar(@PathVariable Long id) {
        return ResponseEntity.ok(facultyService.getAttendanceCalendar(id));
    }

    @GetMapping("/{id}/schedule")
    @Operation(summary = "Get faculty schedule", description = "Retrieve faculty schedule (if HOD)")
    public ResponseEntity<String> getSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(facultyService.getSchedule(id));
    }

    // ============ HOD SPECIFIC ENDPOINTS ============

    @PostMapping("/{id}/schedule")
    @Operation(summary = "Update faculty schedule", description = "Update schedule (HOD only)")
    public ResponseEntity<Map<String, String>> updateSchedule(
            @PathVariable Long id,
            @RequestBody Map<String, Object> scheduleData) {
        facultyService.updateSchedule(id, scheduleData);
        return ResponseEntity.ok(Map.of("message", "Schedule updated successfully"));
    }

    @GetMapping("/department/{department}/all")
    @Operation(summary = "Get all faculty in department", description = "Retrieve all faculty members in specific department")
    public ResponseEntity<List<FacultyDTO>> getFacultyByDepartment(@PathVariable String department) {
        return ResponseEntity.ok(facultyService.getFacultyByDepartment(department));
    }

    @GetMapping("/count")
    @Operation(summary = "Get total faculty count", description = "Get total number of active faculty")
    public ResponseEntity<Map<String, Integer>> getTotalFacultyCount() {
        return ResponseEntity.ok(Map.of("totalFaculty", facultyService.getTotalFacultyCount()));
    }
}