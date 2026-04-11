package com.collage.dashboard.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name = "department-service", url = "http://localhost:8088")
public interface DepartmentServiceClient {

    @GetMapping("/api/departments")
    List<?> getAllDepartments();

    @GetMapping("/api/departments/count/total")
    Map<String, Long> getTotalDepartmentsCount();
}