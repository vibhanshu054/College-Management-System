package com.library.client;

import com.library.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "FACULTY-SERVICE")
public interface FacultyClient {

    @GetMapping("/api/faculty/university-id/{facultyUniversityId}")
    public ResponseEntity<ApiResponse> getFacultyByFacultyUniversityId(@PathVariable String facultyUniversityId);

    @PutMapping("/api/faculty/books/update")
     ResponseEntity<ApiResponse> updateBookStatsByFacultyUniversityId (@RequestParam String facultyUniversityId,
                         @RequestParam int issued,
                         @RequestParam int returned);
}