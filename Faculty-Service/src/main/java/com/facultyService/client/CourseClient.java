package com.facultyService.client;

import com.facultyService.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "COURSE-SERVICE")
public interface CourseClient {

    @GetMapping("/api/courses")
    ResponseEntity<ApiResponse> getAllCourses();

    @GetMapping("/api/courses/faculty/{universityId}")
    List<Map<String, Object>> getCoursesByFaculty(
            @PathVariable String universityId
    );

    @DeleteMapping("/api/courses/faculty/{universityId}")
    void removeFacultyCourses(@PathVariable String universityId);

    @PostMapping("/api/courses/assign/faculty/{facultyUniversityId}")
    public ResponseEntity<ApiResponse> assignCoursesToFacultyByCode(
            @PathVariable String facultyUniversityId,
            @RequestBody List<String> courseCodes);
}