package com.library.client;

import com.library.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "STUDENT-SERVICE")
public interface StudentClient {

    @GetMapping("/api/students/{universityId}")
    Map<String, Object> getStudentById(@PathVariable String universityId);

    @PutMapping("/api/students/books/update/{universityId}")   //  CHANGE
    void updateStudentBooks(
            @PathVariable String universityId,
            @RequestParam int issued,
            @RequestParam int returned
    );
    @GetMapping("api/students/university-id/{universityId}")
    public ResponseEntity<ApiResponse> getStudentByUniversityId(@PathVariable String universityId);
}