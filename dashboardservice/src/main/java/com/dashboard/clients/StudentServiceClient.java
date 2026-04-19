package com.dashboard.clients;

import com.dashboard.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "STUDENT-SERVICE")
public interface StudentServiceClient {

    @GetMapping("/api/students")
    ResponseEntity<ApiResponse> getAllStudents();

    @GetMapping("/api/students/count")
    ResponseEntity<ApiResponse> getTotalStudentsCount();

    @GetMapping("/api/students/university-id/{universityId}")
    ResponseEntity<ApiResponse> getStudentByUniversityId(@PathVariable String universityId);
}