package com.department_Service.service;

import com.department_Service.dto.ApiResponse;
import com.department_Service.dto.DepartmentDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


public interface DepartmentService {

    ApiResponse createDepartment(DepartmentDTO departmentDTO, String createdBy);


    ApiResponse getDepartmentById(Long id);

    ApiResponse getDepartmentByCode(String code);


    ApiResponse getAllDepartments();


    ApiResponse updateDepartment(Long id, DepartmentDTO departmentDTO, String updatedBy);


    ApiResponse deleteDepartment(Long id, String deletedBy);

    ApiResponse updateFacultyCount(Long departmentId, int count);

    ApiResponse updateStudentCount(Long departmentId, int count);


    ApiResponse getTotalDepartmentsCount();
}