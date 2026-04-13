package com.dashboard.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ATTENDANCE-SERVICE")   // Eureka based
public interface AttendanceServiceClient {

    @GetMapping("/attendance/student/{studentId}")  //  FIXED PATH
    List<?> getStudentAttendance(@PathVariable String studentId);
}