package com.dashboard.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@FeignClient(name = "FACULTY-SERVICE")
public interface FacultyServiceClient {

    @GetMapping("/api/faculty/count/total")
    Map<String, Long> getTotalFacultyCount();
}