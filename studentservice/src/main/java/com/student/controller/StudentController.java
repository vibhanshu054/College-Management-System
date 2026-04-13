package com.student.controller;

import com.student.dto.StudentDTO;
import com.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Student Management", description = "APIs for Student Profile and Management")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @Operation(summary = "Create new student", description = "Create student profile with all required fields")
    public ResponseEntity<Long> createStudent(@RequestBody StudentDTO studentDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(studentService.createStudent(studentDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID", description = "Retrieve student profile - READ ONLY")
    public ResponseEntity<StudentDTO> getStudent(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudent(id));
    }

    @GetMapping("/university-id/{universityId}")
    @Operation(summary = "Get student by University ID", description = "Retrieve student profile using university ID")
    public ResponseEntity<StudentDTO> getStudentByUniversityId(@PathVariable String universityId) {
        return ResponseEntity.ok(studentService.getStudentByUniversityId(universityId));
    }

    @GetMapping
    @Operation(summary = "Get all students", description = "Retrieve all students with filters")
    public ResponseEntity<List<StudentDTO>> getAllStudents(
            @RequestParam(required = false) String courseCode,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String department) {
        return ResponseEntity.ok(studentService.getAllStudents(courseCode, semester, department));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update student profile", description = "Update student information")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable Long id, @RequestBody StudentDTO studentDTO) {
        return ResponseEntity.ok(studentService.updateStudent(id, studentDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete student", description = "Soft delete student")
    public ResponseEntity<Map<String, String>> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(Map.of("message", "Student deleted successfully"));
    }

    @GetMapping("/{id}/dashboard")
    @Operation(summary = "Get student dashboard", description = "Get student profile, book counts, and attendance details")
    public ResponseEntity<Map<String, Object>> getStudentDashboard(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentDashboard(id));
    }

    @GetMapping("/{id}/attendance")
    @Operation(summary = "Get student attendance calendar", description = "Retrieve attendance record - READ ONLY")
    public ResponseEntity<List<Map<String, Object>>> getAttendanceCalendar(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getAttendanceCalendar(id));
    }

    @GetMapping("/{id}/books-status")
    @Operation(summary = "Get student books status", description = "Total books issued and returned")
    public ResponseEntity<Map<String, Integer>> getBooksStatus(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getBooksStatus(id));
    }

    @GetMapping("/filter/by-course/{courseCode}")
    @Operation(summary = "Get students by course code", description = "Retrieve all students in specific course")
    public ResponseEntity<List<StudentDTO>> getStudentsByCourse(@PathVariable String courseCode) {
        return ResponseEntity.ok(studentService.getStudentsByCourse(courseCode));
    }

    @GetMapping("/filter/by-department/{department}")
    @Operation(summary = "Get students by department", description = "Retrieve all students in specific department")
    public ResponseEntity<List<StudentDTO>> getStudentsByDepartment(@PathVariable String department) {
        return ResponseEntity.ok(studentService.getStudentsByDepartment(department));
    }

    @GetMapping("/count")
    @Operation(summary = "Get total students count", description = "Get total number of students")
    public ResponseEntity<Map<String, Long>> getTotalStudentsCount() {
        return ResponseEntity.ok(Map.of("totalStudents", studentService.getTotalStudentsCount()));
    }
}