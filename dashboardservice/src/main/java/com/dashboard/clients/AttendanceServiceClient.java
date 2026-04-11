package com.collage.dashboard.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "attendance-service", url = "http://localhost:8090")
public interface AttendanceServiceClient {

    @GetMapping("/api/attendance/student/{studentId}")
    List<?> getStudentAttendance(@PathVariable String studentId);
}