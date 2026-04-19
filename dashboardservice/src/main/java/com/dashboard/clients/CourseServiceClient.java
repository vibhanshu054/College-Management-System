package com.dashboard.clients;


import com.dashboard.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "COURSE-SERVICE")
public interface CourseServiceClient {

    @GetMapping("/api/courses")
    ResponseEntity<ApiResponse> getAllCourses();
    @GetMapping("/api/courses/count")
    ResponseEntity<ApiResponse> getTotalCoursesCount();
    @GetMapping("/api/courses/faculty/{universityId}")
    ResponseEntity<ApiResponse> getCoursesByFaculty(@PathVariable String universityId);
}