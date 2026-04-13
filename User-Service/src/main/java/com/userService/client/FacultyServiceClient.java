package com.userService.client;

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
    ResponseEntity<FacultyDTO> createFacultyFromUser(
            @RequestBody FacultyDTO facultyDTO
    );

    @DeleteMapping("/api/faculty/{id}")
    ResponseEntity<Void> deleteFaculty(
            @PathVariable Long id
    );

    @PutMapping("/api/faculty/{id}")
    ResponseEntity<FacultyDTO> updateFaculty(
            @PathVariable Long id,
            @RequestBody FacultyDTO dto
    );
}