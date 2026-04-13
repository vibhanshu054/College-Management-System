package com.subject_Service.service;


import com.subject_Service.dto.SubjectDTO;
import com.subject_Service.entity.SubjectEntity;
import com.subject_Service.exception.*;
import com.subject_Service.exception.DuplicateResourceException;
import com.subject_Service.exception.ResourceNotFoundException;
import com.subject_Service.repository.SubjectRepository;
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
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final ModelMapper modelMapper;

    /**
     * Create a new subject
     */
    public SubjectDTO createSubject(SubjectDTO subjectDTO, String createdBy) {
        log.info("Creating subject: {} with code: {}", subjectDTO.getSubjectName(),
                subjectDTO.getSubjectCode());

        if (subjectRepository.findBySubjectCode(subjectDTO.getSubjectCode()).isPresent()) {
            log.warn("Duplicate subject code: {}", subjectDTO.getSubjectCode());
            throw new DuplicateResourceException("Subject code already exists");
        }

        SubjectEntity subject = modelMapper.map(subjectDTO, SubjectEntity.class);
        subject.setCreatedBy(createdBy);
        subject.setUpdatedBy(createdBy);

        SubjectEntity savedSubject = subjectRepository.save(subject);
        log.info("Subject created with ID: {}", savedSubject.getId());

        return modelMapper.map(savedSubject, SubjectDTO.class);
    }

    /**
     * Get subject by ID
     */
    @Transactional(readOnly = true)
    public SubjectDTO getSubjectById(Long id) {
        log.debug("Fetching subject with ID: {}", id);
        SubjectEntity subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + id));
        return modelMapper.map(subject, SubjectDTO.class);
    }

    /**
     * Get subject by code
     */
    @Transactional(readOnly = true)
    public SubjectDTO getSubjectByCode(String code) {
        log.debug("Fetching subject with code: {}", code);
        SubjectEntity subject = subjectRepository.findBySubjectCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with code: " + code));
        return modelMapper.map(subject, SubjectDTO.class);
    }

    /**
     * Get all subjects by course
     */
    @Transactional(readOnly = true)
    public List<SubjectDTO> getSubjectsByCourse(String courseId) {
        log.debug("Fetching subjects for course: {}", courseId);
        return subjectRepository.findByCourseId(courseId)
                .stream()
                .map(subject -> modelMapper.map(subject, SubjectDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Get all subjects by department
     */
    @Transactional(readOnly = true)
    public List<SubjectDTO> getSubjectsByDepartment(String departmentId) {
        log.debug("Fetching subjects for department: {}", departmentId);
        return subjectRepository.findByDepartmentId(departmentId)
                .stream()
                .map(subject -> modelMapper.map(subject, SubjectDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Get all subjects by semester
     */
    @Transactional(readOnly = true)
    public List<SubjectDTO> getSubjectsBySemester(Integer semester) {
        log.debug("Fetching subjects for semester: {}", semester);
        return subjectRepository.findBySemester(semester)
                .stream()
                .map(subject -> modelMapper.map(subject, SubjectDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Get all subjects assigned to faculty
     */
    @Transactional(readOnly = true)
    public List<SubjectDTO> getSubjectsByFaculty(String facultyId) {
        log.debug("Fetching subjects for faculty: {}", facultyId);
        return subjectRepository.findByFacultyId(facultyId)
                .stream()
                .map(subject -> modelMapper.map(subject, SubjectDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Get all active subjects
     */
    @Transactional(readOnly = true)
    public List<SubjectDTO> getAllSubjects() {
        log.debug("Fetching all active subjects");
        return subjectRepository.findAllActive()
                .stream()
                .map(subject -> modelMapper.map(subject, SubjectDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Update subject
     */
    public SubjectDTO updateSubject(Long id, SubjectDTO subjectDTO, String updatedBy) {
        log.info("Updating subject with ID: {}", id);

        SubjectEntity subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + id));

        if (subjectDTO.getSubjectName() != null) {
            subject.setSubjectName(subjectDTO.getSubjectName());
        }
        if (subjectDTO.getDescription() != null) {
            subject.setDescription(subjectDTO.getDescription());
        }
        if (subjectDTO.getFacultyId() != null) {
            subject.setFacultyId(subjectDTO.getFacultyId());
        }
        if (subjectDTO.getFacultyName() != null) {
            subject.setFacultyName(subjectDTO.getFacultyName());
        }
        if (subjectDTO.getCredits() != null) {
            subject.setCredits(subjectDTO.getCredits());
        }

        subject.setUpdatedBy(updatedBy);
        subject.setUpdatedAt(LocalDateTime.now());

        SubjectEntity updatedSubject = subjectRepository.save(subject);
        log.info("Subject updated successfully with ID: {}", updatedSubject.getId());

        return modelMapper.map(updatedSubject, SubjectDTO.class);
    }

    /**
     * Delete subject (soft delete)
     */
    public void deleteSubject(Long id, String deletedBy) {
        log.info("Deleting subject with ID: {}", id);

        SubjectEntity subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + id));

        subject.setActive(false);
        subject.setUpdatedBy(deletedBy);
        subject.setUpdatedAt(LocalDateTime.now());
        subjectRepository.save(subject);

        log.info("Subject marked as inactive with ID: {}", id);
    }

    /**
     * Assign faculty to subject
     */
    public void assignFacultyToSubject(Long subjectId, String facultyId, String facultyName) {
        log.info("Assigning faculty {} to subject {}", facultyId, subjectId);

        SubjectEntity subject = subjectRepository.findById(subjectId).orElse(null);
        if (subject != null) {
            subject.setFacultyId(facultyId);
            subject.setFacultyName(facultyName);
            subject.setUpdatedAt(LocalDateTime.now());
            subjectRepository.save(subject);
        }
    }

    /**
     * Update student enrollment count
     */
    public void updateStudentCount(Long subjectId, Integer count) {
        log.debug("Updating student count for subject: {}", subjectId);
        SubjectEntity subject = subjectRepository.findById(subjectId).orElse(null);
        if (subject != null) {
            subject.setTotalStudentsEnrolled(count);
            subjectRepository.save(subject);
        }
    }
}