package com.department_Service.service.impl;

import com.department_Service.dto.ApiResponse;
import com.department_Service.dto.DepartmentDTO;
import com.department_Service.entity.DepartmentEntity;
import com.department_Service.exception.DuplicateResourceException;
import com.department_Service.exception.ResourceNotFoundException;
import com.department_Service.repository.DepartmentRepository;
import com.department_Service.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;

    /**
     * Create a new department
     */
    @Override
    public ApiResponse createDepartment(DepartmentDTO departmentDTO, String createdBy) {

        log.info("Creating department: {}", departmentDTO.getDepartmentName());

        if (departmentDTO == null || departmentDTO.getDepartmentName() == null) {
            throw new IllegalArgumentException("Department name required");
        }

        //  AUTO CODE GENERATE
        String generatedCode = generateDepartmentCode(departmentDTO.getDepartmentName());

        if (departmentRepository.findByDepartmentName(departmentDTO.getDepartmentName()).isPresent()) {
            throw new DuplicateResourceException("Department name already exists");
        }

        DepartmentEntity department = modelMapper.map(departmentDTO, DepartmentEntity.class);

        department.setDepartmentCode(generatedCode);
        department.setCreatedBy(createdBy);
        department.setUpdatedBy(createdBy);

        DepartmentEntity saved = departmentRepository.save(department);

        return new ApiResponse(
                "Department created",
                201,
                modelMapper.map(saved, DepartmentDTO.class),
                LocalDateTime.now()
        );
    }

    /**
     * Get department by ID
     */
    @Transactional(readOnly = true)
    @Override
    public ApiResponse getDepartmentById(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Department id required");
        }

        DepartmentEntity department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        return new ApiResponse(
                "Department fetched",
                200,
                modelMapper.map(department, DepartmentDTO.class),
                LocalDateTime.now()
        );
    }

    /**
     * Get department by code
     */
    @Override
    @Transactional(readOnly = true)
    public ApiResponse getDepartmentByCode(String code) {

        log.debug("Fetching department with code: {}", code);

        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Department code required");
        }

        DepartmentEntity department = departmentRepository.findByDepartmentCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        return new ApiResponse(
                "Department fetched",
                200,
                modelMapper.map(department, DepartmentDTO.class),
                LocalDateTime.now()
        );
    }

    /**
     * Get all active departments
     */
    @Transactional(readOnly = true)
    @Override
    public ApiResponse getAllDepartments() {

        List<DepartmentDTO> list = departmentRepository.findAllActive()
                .stream()
                .map(dept -> modelMapper.map(dept, DepartmentDTO.class))
                .collect(Collectors.toList());

        return new ApiResponse(
                "Departments fetched",
                200,
                list,
                LocalDateTime.now()
        );
    }

    /**
     * Update department
     */
    @Override
    public ApiResponse updateDepartment(Long id, DepartmentDTO dto, String updatedBy) {

        DepartmentEntity department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        if (dto.getDepartmentName() != null) {
            department.setDepartmentName(dto.getDepartmentName());
        }

        if (dto.getDescription() != null) {
            department.setDescription(dto.getDescription());
        }

        department.setUpdatedBy(updatedBy);
        department.setUpdatedAt(LocalDateTime.now());

        DepartmentEntity updated = departmentRepository.save(department);

        return new ApiResponse(
                "Department updated",
                200,
                modelMapper.map(updated, DepartmentDTO.class),
                LocalDateTime.now()
        );
    }

    /**
     * Delete department (soft delete)
     */
    @Override
    public ApiResponse deleteDepartment(Long id, String deletedBy) {

        log.info("Deleting department {}", id);

        DepartmentEntity department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        department.setActive(false);
        department.setUpdatedBy(deletedBy);
        department.setUpdatedAt(LocalDateTime.now());

        departmentRepository.save(department);

        return new ApiResponse(
                "Department deleted",
                200,
                null,
                LocalDateTime.now()
        );
    }

    /**
     * Update faculty count
     */
    @Override
    public ApiResponse updateFacultyCount(Long departmentId, int count) {

        log.debug("Updating faculty count for department: {}", departmentId);

        if (departmentId == null || count < 0) {
            throw new IllegalArgumentException("Invalid input");
        }

        DepartmentEntity department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        department.setTotalFaculty(count);
        department.setUpdatedAt(LocalDateTime.now());

        departmentRepository.save(department);

        return new ApiResponse(
                "Faculty count updated",
                200,
                null,
                LocalDateTime.now()
        );
    }

    /**
     * Update student count
     */
    @Override
    public ApiResponse updateStudentCount(Long departmentId, int count) {

        log.debug("Updating student count for department: {}", departmentId);

        if (departmentId == null || count < 0) {
            throw new IllegalArgumentException("Invalid input");
        }

        DepartmentEntity department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        department.setTotalStudents(count);
        department.setUpdatedAt(LocalDateTime.now());

        departmentRepository.save(department);

        return new ApiResponse(
                "Student count updated",
                200,
                null,
                LocalDateTime.now()
        );
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse getTotalDepartmentsCount() {

        long count = departmentRepository.countActiveOnly();

        return new ApiResponse(
                "Total departments",
                200,
                Map.of("totalDepartments", count),
                LocalDateTime.now()
        );
    }

    private String generateDepartmentCode(String departmentName) {

        // first 3 char
        String prefix = departmentName.length() >= 3
                ? departmentName.substring(0, 3).toUpperCase()
                : departmentName.toUpperCase();
        // count same prefix
        long count = departmentRepository.countByPrefix(prefix) + 1;

        // 3 digit format → 001
        return prefix + String.format("%03d", count);
    }
}