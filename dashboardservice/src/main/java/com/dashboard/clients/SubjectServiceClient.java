package com.dashboard.clients;

import com.dashboard.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@FeignClient(name = "SUBJECT-SERVICE")
public interface SubjectServiceClient {

    @GetMapping("/api/subjects")
    ResponseEntity<ApiResponse> getAllSubjects();

    @GetMapping("/api/subjects/student/{universityId}")
    ResponseEntity<ApiResponse> getSubjectsByStudentUniversityId(@PathVariable String universityId);
}