package com.facultyService.controller;

import com.facultyService.dto.ApiResponse;
import com.facultyService.dto.FacultyDTO;
import com.facultyService.service.FacultyService;
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
@Tag(name = "Faculty Management", description = "Faculty APIs")
public class FacultyController {

    private final FacultyService facultyService;

    // ================= PROFILE =================

    @PostMapping
    public ResponseEntity<ApiResponse> createFaculty(@RequestBody FacultyDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(facultyService.createFaculty(dto));
    }

    @GetMapping("/{facultyUniversityId}")
    public ResponseEntity<ApiResponse> getFaculty(@PathVariable String facultyUniversityId) {
        return ResponseEntity.ok(facultyService.getFaculty(facultyUniversityId));
    }

    @GetMapping("/university-id/{facultyUniversityId}")
    public ResponseEntity<ApiResponse> getFacultyByFacultyUniversityId(@PathVariable String facultyUniversityId) {
        return ResponseEntity.ok(facultyService.getFacultyByFacultyUniversityId(facultyUniversityId));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllFaculty(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String subRole) {

        return ResponseEntity.ok(facultyService.getAllFaculty(department, subRole));
    }

    @PutMapping("/{facultyUniversityId}")
    public ResponseEntity<ApiResponse> updateFaculty(@PathVariable String facultyUniversityId,
                                                     @RequestBody FacultyDTO dto) {
        return ResponseEntity.ok(facultyService.updateFaculty(facultyUniversityId, dto));
    }

    @DeleteMapping("/{facultyUniversityId}")
    public ResponseEntity<ApiResponse> deleteFaculty(@PathVariable String facultyUniversityId) {
        return ResponseEntity.ok(facultyService.deleteFaculty(facultyUniversityId));
    }

    // ================= BOOKS =================

    @PutMapping("/books/update")
    public ResponseEntity<ApiResponse> updateBookStatsByFacultyUniversityId(
            @RequestParam String facultyUniversityId,
            @RequestParam int issued,
            @RequestParam int returned) {

        return ResponseEntity.ok(
                facultyService.updateBookStatsByFacultyUniversityId(facultyUniversityId, issued, returned)
        );
    }

    // ================= DASHBOARD =================

    @GetMapping("/{facultyUniversityId}/dashboard")
    public ResponseEntity<ApiResponse> getDashboard(@PathVariable String facultyUniversityId) {
        return ResponseEntity.ok(facultyService.getDashboard(facultyUniversityId));
    }

    @GetMapping("/{facultyUniversityId}/lectures")
    public ResponseEntity<ApiResponse> getLectures(@PathVariable String facultyUniversityId) {
        return ResponseEntity.ok(facultyService.getAttendanceById(facultyUniversityId));
    }

    @GetMapping("/{facultyUniversityId}/attendance-summary")
    public ResponseEntity<ApiResponse> getAttendance(@PathVariable String facultyUniversityId) {
        return ResponseEntity.ok(facultyService.getAttendanceById(facultyUniversityId));
    }

    // ================= COURSES =================

    @GetMapping("/{facultyUniversityId}/courses")
    public ResponseEntity<ApiResponse> getCourses(@PathVariable String facultyUniversityId) {
        return ResponseEntity.ok(facultyService.getCoursesById(facultyUniversityId));
    }

    @GetMapping("/courses/{facultyUniversityId}")
    public ResponseEntity<ApiResponse> getAssignedCourses(@PathVariable String facultyUniversityId) {
        return ResponseEntity.ok(facultyService.getAssignedCourses(facultyUniversityId));
    }

    @PutMapping("/{facultyUniversityId}/assign-course")
    public ResponseEntity<ApiResponse> assignCourse(
            @PathVariable String facultyUniversityId,
            @RequestBody List<Long> courseIds) {

        return ResponseEntity.ok(
                facultyService.assignCoursesById(facultyUniversityId, courseIds)
        );
    }

    // ================= STUDENTS =================

    @GetMapping("/{facultyUniversityId}/students")
    public ResponseEntity<ApiResponse> getStudents(@PathVariable String facultyUniversityId) {
        return ResponseEntity.ok(facultyService.getStudentsById(facultyUniversityId));
    }

    @GetMapping("/students/{facultyUniversityId}")
    public ResponseEntity<ApiResponse> getStudentsByFacultyId(@PathVariable String facultyUniversityId) {
        return ResponseEntity.ok(facultyService.getStudents(facultyUniversityId));
    }

    @GetMapping("/students/count/{facultyUniversityId}")
    public ResponseEntity<ApiResponse> getTotalStudents(@PathVariable String facultyUniversityId) {
        return ResponseEntity.ok(facultyService.getTotalStudents(facultyUniversityId));
    }

    @GetMapping("/{facultyUniversityId}/students/count")
    public ResponseEntity<ApiResponse> getStudentCount(@PathVariable String facultyUniversityId) {
        return ResponseEntity.ok(facultyService.getStudentCountById(facultyUniversityId));
    }

    // ================= SCHEDULE =================

    @PutMapping("/{facultyUniversityId}/assign-schedule")
    public ResponseEntity<ApiResponse> assignSchedule(
            @PathVariable String facultyUniversityId,
            @RequestBody Map<String, Object> schedule) {

        return ResponseEntity.ok(
                facultyService.assignScheduleById(facultyUniversityId, schedule)
        );
    }

    @GetMapping("/{facultyUniversityId}/schedule")
    public ResponseEntity<ApiResponse> getSchedule(@PathVariable String facultyUniversityId) {
        return ResponseEntity.ok(facultyService.getSchedule(facultyUniversityId));
    }

    // ================= ATTENDANCE =================

    @GetMapping("/{facultyUniversityId}/attendance-calendar")
    public ResponseEntity<ApiResponse> getAttendanceCalendar(@PathVariable String facultyUniversityId) {
        return ResponseEntity.ok(facultyService.getAttendanceCalendar(facultyUniversityId));
    }

    @PostMapping("/attendance/mark")
    public ResponseEntity<ApiResponse> markAttendance(
            @RequestParam String facultyUniversityId,
            @RequestParam String status,
            @RequestParam Long facultyId,
            @RequestParam String courseCode) {

        return ResponseEntity.ok(
                facultyService.markAttendance(
                        facultyUniversityId,
                        com.student.enums.AttendanceStatus.valueOf(status),
                        facultyId,
                        courseCode
                )
        );
    }

    @PostMapping("/attendance/self")
    public ResponseEntity<ApiResponse> markSelfAttendance(
            @RequestParam String facultyUniversityId,
            @RequestParam Long facultyId) {

        return ResponseEntity.ok(
                facultyService.markFacultySelfAttendance(facultyUniversityId, facultyId)
        );
    }
}