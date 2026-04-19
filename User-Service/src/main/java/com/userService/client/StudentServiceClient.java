package com.userService.client;

import com.userService.dto.StudentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "STUDENT-SERVICE",   //  uppercase
        fallback = StudentServiceClientFallback.class
)
public interface StudentServiceClient {

    @PostMapping("/api/students")   //  FIX
    ResponseEntity<StudentDTO> createStudentFromUser(
            @RequestBody StudentDTO studentDTO
    );

    @DeleteMapping("/api/students/{universityId}")
    ResponseEntity<Void> deleteStudent(
            @PathVariable String universityId
    );

    @PutMapping("/api/students/{universityId}")
    ResponseEntity<StudentDTO> updateStudent(
            @PathVariable String universityId,
            @RequestBody StudentDTO dto
    );
}