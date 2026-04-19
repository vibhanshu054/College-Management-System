package com.userService.client;

import com.userService.dto.ApiResponse;
import com.userService.dto.FacultyDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@FeignClient(
        name = "Faculty-Service",
        fallback = FacultyServiceClientFallback.class
)
public interface FacultyServiceClient {

    @PostMapping("/api/faculty")   //  FIXED
    ResponseEntity<ApiResponse> createFacultyFromUser(
            @RequestBody FacultyDTO facultyDTO
    );

    @DeleteMapping("/api/faculty/{facultyUniversityId}")
    ResponseEntity<ApiResponse> deleteFaculty(
            @PathVariable String facultyUniversityId
    );

    @PutMapping("/api/faculty/{facultyUniversityId}")
    ResponseEntity<ApiResponse> updateFaculty(
            @PathVariable String facultyUniversityId,
            @RequestBody FacultyDTO dto
    );
}