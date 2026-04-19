package com.student.client;

import com.student.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SUBJECT-SERVICE")
public interface SubjectServiceClient {

    @GetMapping("/api/subjects/code/{code}")
    ResponseEntity<ApiResponse> getSubjectByCode(@PathVariable String code);
}
