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

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService service;

    // CREATE
    @PostMapping
    public ResponseEntity<ApiResponse> createCourse(@Valid @RequestBody CourseRequestDto dto) {
        log.info("Request received to create course with name: {}", dto.getName());

        Course course = service.createCourse(dto);

        log.info("Course created successfully with ID: {}", course.getId());

        ApiResponse response = new ApiResponse(
                "Course created successfully",
                201,
                LocalDateTime.now()
        );

        return ResponseEntity.status(201).body(response);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        log.info("Request received to fetch all courses");

        List<Course> courses = service.getAllCourses();

        log.info("Fetched {} courses", courses.size());

        return ResponseEntity.ok(courses);
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourse(@PathVariable Long id) {
        log.info("Request received to fetch course with ID: {}", id);

        Course course = service.getCourseById(id);

        log.info("Course fetched successfully with ID: {}", id);

        return ResponseEntity.ok(course);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCourse(@PathVariable Long id,
                                                    @Valid @RequestBody CourseRequestDto dto) {
        log.info("Request received to update course with ID: {}", id);

        Course updated = service.updateCourse(id, dto);

        log.info("Course updated successfully with ID: {}", updated.getId());

        ApiResponse response = new ApiResponse(
                "Course updated successfully",
                200,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCourse(@PathVariable Long id) {
        log.info("Request received to delete course with ID: {}", id);

        service.deleteCourse(id);

        log.info("Course deleted successfully with ID: {}", id);

        ApiResponse response = new ApiResponse(
                "Course deleted successfully",
                200,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }
}