package com.department_Service.controller;


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

    @PostMapping
    @Operation(summary = "Create department", description = "Admin only - Create new department")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<DepartmentDTO> createDepartment(
            @Valid @RequestBody DepartmentDTO departmentDTO,
            Authentication auth) {
        log.info("Create department request from: {}", auth.getName());
        DepartmentDTO created = departmentService.createDepartment(departmentDTO, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID", description = "Retrieve department details")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<DepartmentDTO> getDepartment(@PathVariable Long id) {
        log.info("Get department {} request", id);
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get department by code", description = "Retrieve department using department code")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<DepartmentDTO> getDepartmentByCode(@PathVariable String code) {
        log.info("Get department by code: {}", code);
        return ResponseEntity.ok(departmentService.getDepartmentByCode(code));
    }

    @GetMapping
    @Operation(summary = "Get all departments", description = "Retrieve all active departments")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {
        log.info("Get all departments request");
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update department", description = "Update department information")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<DepartmentDTO> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentDTO departmentDTO,
            Authentication auth) {
        log.info("Update department {} request from: {}", id, auth.getName());
        DepartmentDTO updated = departmentService.updateDepartment(id, departmentDTO, auth.getName());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete department", description = "Delete department from system")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, String>> deleteDepartment(
            @PathVariable Long id,
            Authentication auth) {
        log.info("Delete department {} request from: {}", id, auth.getName());
        departmentService.deleteDepartment(id, auth.getName());
        return ResponseEntity.ok(Map.of("message", "Department deleted successfully"));
    }

    @GetMapping("/count/total")
    @Operation(summary = "Get total departments", description = "Get count of all active departments")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Long>> getTotalCount() {
        log.info("Get total departments count request");
        return ResponseEntity.ok(Map.of("totalDepartments", departmentService.getTotalDepartmentsCount()));
    }
}