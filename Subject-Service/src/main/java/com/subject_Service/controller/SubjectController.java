package com.subject_Service.controller;



import com.subject_Service.dto.ApiResponse;
import com.subject_Service.dto.SubjectDTO;
import com.subject_Service.service.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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


        public ResponseEntity<ApiResponse> createSubject(
                @Valid @RequestBody SubjectDTO subjectDTO,
                Authentication auth) {

        String username = (auth != null && auth.getName() != null)
                ? auth.getName()
                : "SYSTEM";
        log.info("Create subject request from: {}", username);
            return ResponseEntity.status(201)
                    .body(subjectService.createSubject(subjectDTO, username));
        }


    @GetMapping("/{id}")
    @Operation(summary = "Get subject by ID", description = "Retrieve subject details")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse> getSubject(@PathVariable Long id) {
        log.info("Get subject {} request", id);
        return ResponseEntity.ok(subjectService.getSubjectById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get subject by code", description = "Retrieve subject using subject code")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse> getSubjectByCode(@PathVariable String code) {
        log.info("Get subject by code: {}", code);
        return ResponseEntity.ok(subjectService.getSubjectByCode(code));
    }

    @GetMapping
    @Operation(summary = "Get all subjects", description = "Retrieve all active subjects")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse> getAllSubjects() {
        log.info("Get all subjects request");
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }


    @Operation(summary = "Get subjects by course", description = "Retrieve all subjects in a course")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/course/{courseCode}")
    public ResponseEntity<ApiResponse> getSubjectsByCourse(@PathVariable String courseCode) {

        return ResponseEntity.ok(subjectService.getSubjectsByCourse(courseCode));
    }


    @Operation(summary = "Get subjects by department", description = "Retrieve all subjects in a department")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/department/{departmentCode}")
    public ResponseEntity<ApiResponse> getSubjectsByDepartment(@PathVariable String departmentCode) {

        List<SubjectDTO> list = subjectService.getSubjectsByDepartment(departmentCode);

        return ResponseEntity.ok(
                new ApiResponse("Subjects fetched", 200, list, java.time.LocalDateTime.now())
        );
    }

    @Operation(summary = "Get subjects by semester", description = "Retrieve all subjects in a semester")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/semester/{semester}")
    public ResponseEntity<ApiResponse> getSubjectsBySemester(@PathVariable Integer semester) {

        List<SubjectDTO> list = subjectService.getSubjectsBySemester(semester);

        return ResponseEntity.ok(
                new ApiResponse("Subjects fetched", 200, list, java.time.LocalDateTime.now())
        );
    }


    @Operation(summary = "Get subjects by faculty", description = "Retrieve all subjects assigned to faculty")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/faculty/{facultyUniversityId}")
    public ResponseEntity<ApiResponse> getSubjectsByFaculty(@PathVariable String facultyUniversityId) {

        List<SubjectDTO> list = subjectService.getSubjectsByFaculty(facultyUniversityId);

        return ResponseEntity.ok(
                new ApiResponse("Subjects fetched", 200, list, java.time.LocalDateTime.now())
        );
    }
    @Operation(summary = "Update subject", description = "Update subject information")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{subjectCode}")
    public ResponseEntity<ApiResponse> updateSubject(
            @PathVariable String subjectCode,
            @Valid @RequestBody SubjectDTO subjectDTO,
            Authentication auth) {

        SubjectDTO updated = subjectService.updateSubject(subjectCode, subjectDTO, auth.getName());

        return ResponseEntity.ok(
                new ApiResponse("Subject updated", 200, updated, java.time.LocalDateTime.now())
        );
    }

    @Operation(summary = "Delete subject", description = "Delete subject from system")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{subjectCode}")
    public ResponseEntity<ApiResponse> deleteSubject(
            @PathVariable String subjectCode,
            Authentication auth) {

        String username = (auth != null && auth.getName() != null)
                ? auth.getName()
                : "SYSTEM";

        log.info("Delete subject {} request from: {}", subjectCode, username);

        ApiResponse response = subjectService.deleteSubject(subjectCode, username);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Assign faculty to subject", description = "Assign a faculty to teach subject")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{subjectCode}/assign-faculty/{facultyUniversityId}")
    public ResponseEntity<ApiResponse> assignFacultyToSubject(
            @PathVariable String subjectCode,
            @PathVariable String facultyUniversityId,
            @RequestParam String facultyName) {

        subjectService.assignFacultyToSubject(subjectCode,facultyUniversityId, facultyName);

        return ResponseEntity.ok(
                new ApiResponse("Faculty assigned successfully", 200, null, java.time.LocalDateTime.now())
        );
    }

    @GetMapping("/student/{universityId}")
    ResponseEntity<ApiResponse> getSubjectsByStudentUniversityId(@PathVariable String universityId){
        log.info("Get subjects for student: {}", universityId);
        return ResponseEntity.ok(subjectService.getSubjectsByStudentUniversityId(universityId));
    }
}