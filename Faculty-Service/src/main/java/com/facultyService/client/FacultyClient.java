package com.facultyService.client;

import com.facultyService.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "FACULTY-SERVICE",url = "http://localhost:8087" )
public interface FacultyClient {

    @GetMapping("/api/faculty/university-id/{facultyUniversityId}")
    ResponseEntity<ApiResponse> getFacultyByFacultyUniversityId(@PathVariable String facultyUniversityId);

}
