package com.facultyService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "ATTENDANCE-SERVICE")
public interface AttendanceClient {

    @GetMapping("/api/attendance/faculty/{universityId}/range")
    List<Map<String, Object>> getFacultyAttendance(
            @PathVariable String universityId,
            @RequestParam String startDate,
            @RequestParam String endDate
    );
}