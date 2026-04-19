package com.facultyService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "COURSE-SERVICE")
public interface CourseClient {

    @GetMapping("/api/courses")
    List<Map<String, Object>> getAllCourses();

    @GetMapping("/api/courses/faculty/{universityId}")
    List<Map<String, Object>> getCoursesByFaculty(
            @PathVariable String universityId
    );
    @DeleteMapping("/api/courses/faculty/{universityId}")
    void removeFacultyCourses(@PathVariable String universityId);

    @PostMapping("/api/courses/assign/{universityId}")
    void assignCoursesToFaculty(
            @PathVariable String universityId,
            @RequestBody List<Long> courseIds
    );
}