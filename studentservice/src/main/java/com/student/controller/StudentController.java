package com.student.controller;

import com.student.dto.ApiResponse;
import com.student.dto.StudentDTO;
import com.student.enums.AttendanceStatus;
import com.student.service.StudentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Student Management", description = "APIs for Student Profile and Management")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<ApiResponse> createStudent(
            @Valid @RequestBody StudentDTO dto,
            @RequestHeader(value = "X-User-Name", required = false) String username,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        username = (username != null && !username.isBlank()) ? username : "ADMIN";
        role = (role!=null && !role.isBlank())? role:"ADMIN";

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(studentService.createStudent(dto, username, role));
    }

    @PutMapping("/{universityId}/semester")
    public ResponseEntity<ApiResponse> updateSemester(
            @PathVariable String universityId,
            @RequestParam String semester,
            @RequestHeader("X-User-Role") String role
    ) {
        return ResponseEntity.ok(studentService.updateSemester(universityId, semester, role));
    }

    @GetMapping("/courses/student-count")
    public ResponseEntity<ApiResponse> getStudentCountByCourse() {
        return ResponseEntity.ok(studentService.getStudentCountByCourse());
    }

    @PutMapping("/{universityId}/subjects")
    public ResponseEntity<ApiResponse> updateSubjects(
            @PathVariable String universityId,
            @RequestBody List<String> subjects,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        return ResponseEntity.ok(studentService.updateSubjects(universityId, subjects, role));
    }

    @PutMapping("/books/update/{universityId}")
    public ResponseEntity<ApiResponse> updateBooks(
            @PathVariable String universityId,
            @RequestParam int issued,
            @RequestParam int returned
    ) {
        return ResponseEntity.ok(studentService.updateBookStats(universityId, issued, returned));
    }

    @GetMapping("/faculty/{facultyUniversityId}")
    public ResponseEntity<ApiResponse> getStudentsByFacultyUniversityId(@PathVariable String facultyUniversityId) {
        return ResponseEntity.ok(studentService.getStudentsByFacultyUniversityId(facultyUniversityId));
    }

    @GetMapping("/university-id/{universityId}")
    public ResponseEntity<ApiResponse> getStudentByUniversityId(@PathVariable String universityId) {
        return ResponseEntity.ok(studentService.getStudentByUniversityId(universityId));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllStudents(
            @RequestParam(required = false) String courseCode,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String department,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        return ResponseEntity.ok(studentService.getAllStudents(courseCode, semester, department, role));
    }

    @PutMapping("/{universityId}")
    public ResponseEntity<ApiResponse> updateStudent(
            @PathVariable String universityId,
            @RequestBody StudentDTO studentDto,
            @RequestHeader(value = "X-User-Name", required = false) String username,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        username = (username != null && !username.isBlank()) ? username : "SYSTEM";

        return ResponseEntity.ok(
                studentService.updateStudent(universityId, studentDto, username, role)
        );
    }

    @DeleteMapping("/{universityId}")
    public ResponseEntity<ApiResponse> deleteStudent(
            @PathVariable String universityId,
            @RequestHeader(value = "X-User-Name", required = false) String username,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        username = (username != null && !username.isBlank()) ? username : "SYSTEM";
        return ResponseEntity.ok(studentService.deleteStudent(universityId, username, role));
    }

    @GetMapping("/{universityId}/dashboard")
    public ResponseEntity<ApiResponse> getStudentDashboard(@PathVariable String universityId) {
        return ResponseEntity.ok(studentService.getStudentDashboard(universityId));
    }

    @GetMapping("/{universityId}/attendance")
    public ResponseEntity<ApiResponse> getAttendanceCalendar(@PathVariable String universityId) {
        return ResponseEntity.ok(studentService.getAttendanceCalendar(universityId));
    }

    @PostMapping("/attendance")
    public ResponseEntity<ApiResponse> markAttendance(
            @RequestParam String universityId,
            @RequestParam AttendanceStatus status,
            @RequestParam Long facultyId,
            @RequestParam String courseCode
    ) {
        return ResponseEntity.ok(studentService.markAttendance(universityId, status, facultyId, courseCode));
    }

    @GetMapping("/{universityId}/books-status")
    public ResponseEntity<ApiResponse> getBooksStatus(@PathVariable String universityId) {
        return ResponseEntity.ok(studentService.getBooksStatus(universityId));
    }

    @GetMapping("/filter/by-course/{courseCode}")
    public ResponseEntity<ApiResponse> getStudentsByCourse(@PathVariable String courseCode) {
        return ResponseEntity.ok(studentService.getStudentsByCourse(courseCode));
    }

    @GetMapping("/filter/by-department/{department}")
    public ResponseEntity<ApiResponse> getStudentsByDepartment(@PathVariable String department) {
        return ResponseEntity.ok(studentService.getStudentsByDepartment(department));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse> getTotalStudentsCount() {
        return ResponseEntity.ok(studentService.getTotalStudentsCount());
    }
}