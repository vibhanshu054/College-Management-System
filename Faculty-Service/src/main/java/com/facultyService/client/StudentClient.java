package com.facultyService.client;

import com.facultyService.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "STUDENT-SERVICE")
public interface StudentClient {
    @GetMapping("/api/students/faculty/{facultyUniversityId}")
    ApiResponse getStudentsByFacultyUniversityId(@PathVariable String facultyUniversityId);

    @DeleteMapping("/api/students/faculty/{universityId}")
    void removeFacultyFromStudents(@PathVariable String universityId);
    @PostMapping("/api/students/attendance")
    ApiResponse markAttendance(
            @RequestParam String universityId,
            @RequestParam String status,
           @RequestParam Long facultyId, @RequestParam String courseCode
    );

}