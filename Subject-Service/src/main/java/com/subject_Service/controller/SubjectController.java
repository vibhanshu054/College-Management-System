package com.subject_Service.controller;



import com.subject_Service.dto.SubjectDTO;
import com.subject_Service.service.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Subject Management", description = "APIs for Subject Management")
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    @Operation(summary = "Create subject", description = "Admin/Faculty - Create new subject")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<SubjectDTO> createSubject(
            @Valid @RequestBody SubjectDTO subjectDTO,
            Authentication auth) {
        log.info("Create subject request from: {}", auth.getName());
        SubjectDTO created = subjectService.createSubject(subjectDTO, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get subject by ID", description = "Retrieve subject details")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<SubjectDTO> getSubject(@PathVariable Long id) {
        log.info("Get subject {} request", id);
        return ResponseEntity.ok(subjectService.getSubjectById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get subject by code", description = "Retrieve subject using subject code")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<SubjectDTO> getSubjectByCode(@PathVariable String code) {
        log.info("Get subject by code: {}", code);
        return ResponseEntity.ok(subjectService.getSubjectByCode(code));
    }

    @GetMapping
    @Operation(summary = "Get all subjects", description = "Retrieve all active subjects")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<SubjectDTO>> getAllSubjects() {
        log.info("Get all subjects request");
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get subjects by course", description = "Retrieve all subjects in a course")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<SubjectDTO>> getSubjectsByCourse(@PathVariable String courseId) {
        log.info("Get subjects for course: {}", courseId);
        return ResponseEntity.ok(subjectService.getSubjectsByCourse(courseId));
    }

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get subjects by department", description = "Retrieve all subjects in a department")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<SubjectDTO>> getSubjectsByDepartment(@PathVariable String departmentId) {
        log.info("Get subjects for department: {}", departmentId);
        return ResponseEntity.ok(subjectService.getSubjectsByDepartment(departmentId));
    }

    @GetMapping("/semester/{semester}")
    @Operation(summary = "Get subjects by semester", description = "Retrieve all subjects in a semester")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<SubjectDTO>> getSubjectsBySemester(@PathVariable Integer semester) {
        log.info("Get subjects for semester: {}", semester);
        return ResponseEntity.ok(subjectService.getSubjectsBySemester(semester));
    }

    @GetMapping("/faculty/{facultyId}")
    @Operation(summary = "Get subjects by faculty", description = "Retrieve all subjects assigned to faculty")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<SubjectDTO>> getSubjectsByFaculty(@PathVariable String facultyId) {
        log.info("Get subjects for faculty: {}", facultyId);
        return ResponseEntity.ok(subjectService.getSubjectsByFaculty(facultyId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update subject", description = "Update subject information")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<SubjectDTO> updateSubject(
            @PathVariable Long id,
            @Valid @RequestBody SubjectDTO subjectDTO,
            Authentication auth) {
        log.info("Update subject {} request from: {}", id, auth.getName());
        SubjectDTO updated = subjectService.updateSubject(id, subjectDTO, auth.getName());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete subject", description = "Delete subject from system")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, String>> deleteSubject(
            @PathVariable Long id,
            Authentication auth) {
        log.info("Delete subject {} request from: {}", id, auth.getName());
        subjectService.deleteSubject(id, auth.getName());
        return ResponseEntity.ok(Map.of("message", "Subject deleted successfully"));
    }

    @PutMapping("/{subjectId}/assign-faculty/{facultyId}")
    @Operation(summary = "Assign faculty to subject", description = "Assign a faculty to teach subject")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, String>> assignFacultyToSubject(
            @PathVariable Long subjectId,
            @PathVariable String facultyId,
            @RequestParam String facultyName) {
        log.info("Assigning faculty {} to subject {}", facultyId, subjectId);
        subjectService.assignFacultyToSubject(subjectId, facultyId, facultyName);
        return ResponseEntity.ok(Map.of("message", "Faculty assigned successfully"));
    }
}