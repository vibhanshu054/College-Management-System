package com.student.client;

import com.student.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "STUDENT-SERVICE")
public interface StudentClient {

    @PostMapping("/api/students/attendance")
    ApiResponse markAttendance(
            @RequestParam String universityId,
            @RequestParam String status,
            @RequestParam Long facultyId,
            @RequestParam String courseCode
    );
}