package com.courseService.controller;

import com.courseService.dto.ApiResponse;
import com.courseService.dto.CourseRequestDto;
import com.courseService.entity.Course;
import com.courseService.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService service;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponse> createCourse(@Valid @RequestBody CourseRequestDto dto) {

        log.info("Request received to create course: {}", dto.getName());

        return ResponseEntity.status(201)
                .body(service.createCourse(dto));
    }

    // ================= ASSIGN TO FACULTY =================
    @PostMapping("/assign/faculty/{facultyUniversityId}")
    public ResponseEntity<ApiResponse> assignCoursesToFacultyByCode(
            @PathVariable String facultyUniversityId,
            @RequestBody List<String> courseCodes) {

        log.info("Assigning courses {} to faculty {}", courseCodes, facultyUniversityId);

        return ResponseEntity.ok(
                service.assignCoursesToFacultyByCode(facultyUniversityId, courseCodes)
        );
    }
    // ================= GET BY FACULTY =================
    @GetMapping("/faculty/{facultyUniversityId}")
    public ResponseEntity<ApiResponse> getCoursesByFaculty(@PathVariable String facultyUniversityId) {

        log.info("Fetching courses for faculty {}", facultyUniversityId);

        return ResponseEntity.ok(
                service.getCoursesByFaculty(facultyUniversityId)
        );
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponse> getAllCourses() {

        List<Course> courses = service.getAllCourses();

        return ResponseEntity.ok(
                new ApiResponse(
                        "Courses fetched",
                        200,
                        courses,
                        LocalDateTime.now()
                )
        );
    }
    @GetMapping("/count")   //  FIRST
    public ResponseEntity<ApiResponse> getTotalCoursesCount() {
        long count = service.getTotalCoursesCount();

        return ResponseEntity.ok(
                new ApiResponse(
                        "Total courses count",
                        200,
                        Map.of("totalCourses", count),
                        LocalDateTime.now()
                )
        );
    }
    // ================= GET BY ID =================

        @GetMapping("/{id}")   // AFTER
        public ResponseEntity<ApiResponse> getCourse(@PathVariable Long id) {

        log.info("Fetching course {}", id);

            return ResponseEntity.ok(service.getCourseById(id));
    }

    // ================= UPDATE =================
    @PutMapping("/{code}")
    public ResponseEntity<ApiResponse> updateCourse(
            @PathVariable String code,
            @Valid @RequestBody CourseRequestDto dto) {

        log.info("Updating course code: {}", code);

        return ResponseEntity.ok(service.updateCourse(code, dto)
        );
    }

    // ================= DELETE =================
    @DeleteMapping("/{code}")
    public ResponseEntity<ApiResponse> deleteCourse(@PathVariable String code) {

        log.info("Deleting course {}", code);

        return ResponseEntity.ok(service.deleteCourse(code));
    }
}