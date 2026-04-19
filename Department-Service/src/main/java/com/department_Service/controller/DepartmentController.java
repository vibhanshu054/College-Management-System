package com.department_Service.controller;


import com.department_Service.dto.ApiResponse;
import com.department_Service.dto.DepartmentDTO;
import com.department_Service.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Department Management", description = "APIs for Department Management")
public class DepartmentController {

    private final DepartmentService departmentService;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponse> createDepartment(
            @Valid @RequestBody DepartmentDTO departmentDTO,
            Authentication auth) {

        String username = (auth != null && auth.getName() != null)
                ? auth.getName()
                : "SYSTEM";

        log.info("Create department request from: {}", username);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(departmentService.createDepartment(departmentDTO, username));
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getDepartment(@PathVariable Long id) {

        log.info("Get department {} request", id);

        return ResponseEntity.ok(
                departmentService.getDepartmentById(id)
        );
    }

    // ================= GET BY CODE =================
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse> getDepartmentByCode(@PathVariable String code) {

        log.info("Get department by code: {}", code);

        return ResponseEntity.ok(
                departmentService.getDepartmentByCode(code)
        );
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<ApiResponse> getAllDepartments() {

        log.info("Get all departments request");

        return ResponseEntity.ok(
                departmentService.getAllDepartments()
        );
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentDTO departmentDTO,
            Authentication auth) {

        log.info("Update department {} request from: {}", id, auth.getName());

        return ResponseEntity.ok(
                departmentService.updateDepartment(id, departmentDTO, auth.getName())
        );
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteDepartment(
            @PathVariable Long id,
            Authentication auth) {

        log.info("Delete department {} request from: {}", id, auth.getName());

        return ResponseEntity.ok(
                departmentService.deleteDepartment(id, auth.getName())
        );
    }

    // ================= COUNT =================
    @GetMapping("/count/total")
    public ResponseEntity<ApiResponse> getTotalCount() {

        log.info("Get total departments count request");

        return ResponseEntity.ok(
                departmentService.getTotalDepartmentsCount()
        );
    }
}