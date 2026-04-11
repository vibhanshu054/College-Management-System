package com.college.department_Service.service;

import com.college.department_Service.dto.DepartmentDTO;
import com.college.department_Service.entity.DepartmentEntity;
import com.college.department_Service.exception.*;
import com.college.department_Service.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;

    /**
     * Create a new department
     */
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO, String createdBy) {
        log.info("Creating department: {} with code: {}", departmentDTO.getDepartmentName(),
                departmentDTO.getDepartmentCode());

        // Check for duplicates
        if (departmentRepository.findByDepartmentCode(departmentDTO.getDepartmentCode()).isPresent()) {
            log.warn("Duplicate department code: {}", departmentDTO.getDepartmentCode());
            throw new DuplicateResourceException("Department code already exists");
        }

        if (departmentRepository.findByDepartmentName(departmentDTO.getDepartmentName()).isPresent()) {
            log.warn("Duplicate department name: {}", departmentDTO.getDepartmentName());
            throw new DuplicateResourceException("Department name already exists");
        }

        DepartmentEntity department = modelMapper.map(departmentDTO, DepartmentEntity.class);
        department.setCreatedBy(createdBy);
        department.setUpdatedBy(createdBy);

        DepartmentEntity savedDepartment = departmentRepository.save(department);
        log.info("Department created with ID: {}", savedDepartment.getId());

        return modelMapper.map(savedDepartment, DepartmentDTO.class);
    }

    /**
     * Get department by ID
     */
    @Transactional(readOnly = true)
    public DepartmentDTO getDepartmentById(Long id) {
        log.debug("Fetching department with ID: {}", id);
        DepartmentEntity department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));
        return modelMapper.map(department, DepartmentDTO.class);
    }

    /**
     * Get department by code
     */
    @Transactional(readOnly = true)
    public DepartmentDTO getDepartmentByCode(String code) {
        log.debug("Fetching department with code: {}", code);
        DepartmentEntity department = departmentRepository.findByDepartmentCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with code: " + code));
        return modelMapper.map(department, DepartmentDTO.class);
    }

    /**
     * Get all active departments
     */
    @Transactional(readOnly = true)
    public List<DepartmentDTO> getAllDepartments() {
        log.debug("Fetching all active departments");
        return departmentRepository.findAllActive()
                .stream()
                .map(dept -> modelMapper.map(dept, DepartmentDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Update department
     */
    public DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDTO, String updatedBy) {
        log.info("Updating department with ID: {}", id);

        DepartmentEntity department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));

        if (departmentDTO.getDepartmentName() != null) {
            department.setDepartmentName(departmentDTO.getDepartmentName());
        }
        if (departmentDTO.getDescription() != null) {
            department.setDescription(departmentDTO.getDescription());
        }
        if (departmentDTO.getHodId() != null) {
            department.setHodId(departmentDTO.getHodId());
        }
        if (departmentDTO.getHodName() != null) {
            department.setHodName(departmentDTO.getHodName());
        }

        department.setUpdatedBy(updatedBy);
        department.setUpdatedAt(LocalDateTime.now());

        DepartmentEntity updatedDepartment = departmentRepository.save(department);
        log.info("Department updated successfully with ID: {}", updatedDepartment.getId());

        return modelMapper.map(updatedDepartment, DepartmentDTO.class);
    }

    /**
     * Delete department (soft delete)
     */
    public void deleteDepartment(Long id, String deletedBy) {
        log.info("Deleting department with ID: {}", id);

        DepartmentEntity department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));

        department.setActive(false);
        department.setUpdatedBy(deletedBy);
        department.setUpdatedAt(LocalDateTime.now());
        departmentRepository.save(department);

        log.info("Department marked as inactive with ID: {}", id);
    }

    /**
     * Update faculty count
     */
    public void updateFacultyCount(Long departmentId, int count) {
        log.debug("Updating faculty count for department: {}", departmentId);
        DepartmentEntity department = departmentRepository.findById(departmentId).orElse(null);
        if (department != null) {
            department.setTotalFaculty(count);
            departmentRepository.save(department);
        }
    }

    /**
     * Update student count
     */
    public void updateStudentCount(Long departmentId, int count) {
        log.debug("Updating student count for department: {}", departmentId);
        DepartmentEntity department = departmentRepository.findById(departmentId).orElse(null);
        if (department != null) {
            department.setTotalStudents(count);
            departmentRepository.save(department);
        }
    }

    /**
     * Get total departments count
     */
    @Transactional(readOnly = true)
    public long getTotalDepartmentsCount() {
        log.debug("Getting total departments count");
        return departmentRepository.countActiveOnly();
    }
}