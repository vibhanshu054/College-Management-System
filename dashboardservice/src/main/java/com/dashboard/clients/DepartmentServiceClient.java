package com.dashboard.clients;

import com.dashboard.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name = "DEPARTMENT-SERVICE")
public interface DepartmentServiceClient {

    @GetMapping("/api/departments")
    ResponseEntity<ApiResponse> getAllDepartments();

    @GetMapping("/api/departments/count/total")
    ResponseEntity<ApiResponse> getTotalDepartmentsCount();
}