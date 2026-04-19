package com.dashboard.clients;

import com.dashboard.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "FACULTY-SERVICE")
public interface FacultyServiceClient {
    // FACULTY SCHEDULE
    @GetMapping("/api/faculty/{facultyUniversityId}/schedule")
    ResponseEntity<ApiResponse> getSchedule(@PathVariable String facultyUniversityId);

    // TOTAL FACULTY
    @GetMapping("/api/faculty")
    ResponseEntity<ApiResponse> getAllFaculty();

    // STUDENTS COUNT BY FACULTY
    @GetMapping("/api/faculty/students/count/{facultyUniversityId}")
    ResponseEntity<ApiResponse> getTotalStudents(@PathVariable String facultyUniversityId);


    @GetMapping("/api/faculty/university-id/{facultyUniversityId}")
    ResponseEntity<ApiResponse> getFacultyByFacultyUniversityId(@PathVariable String facultyUniversityId);


}