package com.subject_Service.service;


import com.subject_Service.dto.ApiResponse;
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
    public ApiResponse createSubject(SubjectDTO dto, String createdBy) {

        if (dto == null) {
            throw new IllegalArgumentException("Subject payload is required");
        }

        if (dto.getSubjectName() == null || dto.getSubjectName().isBlank()) {
            throw new IllegalArgumentException("Subject name is required");
        }

        if (dto.getDepartmentId() == null || dto.getDepartmentId().isBlank()) {
            throw new IllegalArgumentException("Department ID is required");
        }

        if (dto.getDepartmentCode() == null || dto.getDepartmentCode().isBlank()) {
            throw new IllegalArgumentException("Department code is required");
        }

        if (dto.getCourseId() == null || dto.getCourseId().isBlank()) {
            throw new IllegalArgumentException("Course ID is required");
        }

        if (dto.getCourseCode() == null || dto.getCourseCode().isBlank()) {
            throw new IllegalArgumentException("Course code is required");
        }

        if (dto.getCourseName() == null || dto.getCourseName().isBlank()) {
            throw new IllegalArgumentException("Course name is required");
        }

        if (dto.getCredits() == null) {
            throw new IllegalArgumentException("Credits are required");
        }

        if (dto.getSemester() == null) {
            throw new IllegalArgumentException("Semester is required");
        }

        log.info("Creating subject {}", dto.getSubjectName());

        String generatedCode = generateSubjectCode(dto.getDepartmentId(), dto.getSubjectName());

        if (subjectRepository.findBySubjectCode(generatedCode).isPresent()) {
            throw new DuplicateResourceException("Subject already exists");
        }

        SubjectEntity entity = new SubjectEntity();
        entity.setSubjectCode(generatedCode);
        entity.setSubjectName(dto.getSubjectName().trim());
        entity.setCourseId(dto.getCourseId().trim());
        entity.setCourseCode(dto.getCourseCode().trim());
        entity.setCourseName(dto.getCourseName().trim());
        entity.setDepartmentId(dto.getDepartmentId().trim());
        entity.setDepartmentCode(dto.getDepartmentCode().trim());
        entity.setCredits(dto.getCredits());
        entity.setSemester(dto.getSemester());
        entity.setDescription(dto.getDescription());
        entity.setCourseObjectives(dto.getCourseObjectives());
        entity.setOutcomes(dto.getOutcomes());
        entity.setFacultyId(dto.getFacultyId());
        entity.setFacultyName(dto.getFacultyName());
        entity.setStudentUniversityId(dto.getStudentUniversityId());
        entity.setCreatedBy(createdBy);
        entity.setUpdatedBy(createdBy);
        entity.setActive(true);

        SubjectEntity saved = subjectRepository.save(entity);

        return new ApiResponse(
                "Subject created",
                201,
                modelMapper.map(saved, SubjectDTO.class),
                LocalDateTime.now()
        );
    }

    /**
     * Get subject by code
     */
    @Transactional(readOnly = true)
    public ApiResponse getSubjectByCode(String code) {

        SubjectEntity subject = subjectRepository.findBySubjectCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + code));

        return new ApiResponse(
                "Subject fetched",
                200,
                modelMapper.map(subject, SubjectDTO.class),
                LocalDateTime.now()
        );
    }
    public ApiResponse getSubjectById(Long id) {

        log.debug("Fetching subject {}", id);

        SubjectEntity subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        return new ApiResponse(
                "Subject fetched",
                200,
                modelMapper.map(subject, SubjectDTO.class),
                LocalDateTime.now()
        );
    }
    /**
     * Get all subjects by course
     */
    @Transactional(readOnly = true)
    public ApiResponse getSubjectsByCourse(String courseCode) {

        List<SubjectDTO> list = subjectRepository.findByCourseId(courseCode)
                .stream()
                .map(subject -> modelMapper.map(subject, SubjectDTO.class))
                .toList();

        return new ApiResponse(
                "Subjects fetched",
                200,
                list,
                LocalDateTime.now()
        );
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
    public ApiResponse getAllSubjects() {

        log.debug("Fetching all subjects");

        List<SubjectDTO> list = subjectRepository.findAllActive()
                .stream()
                .map(s -> modelMapper.map(s, SubjectDTO.class))
                .toList();

        return new ApiResponse(
                "Subjects fetched",
                200,
                list,
                LocalDateTime.now()
        );
    }

    /**
     * Update subject
     */
    public SubjectDTO updateSubject(String subjectCode, SubjectDTO subjectDTO, String updatedBy) {

        SubjectEntity subject = subjectRepository.findBySubjectCode(subjectCode)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + subjectCode));

        if (subjectDTO.getSubjectName() != null)
            subject.setSubjectName(subjectDTO.getSubjectName());

        if (subjectDTO.getDescription() != null)
            subject.setDescription(subjectDTO.getDescription());

        if (subjectDTO.getFacultyId() != null)
            subject.setFacultyId(subjectDTO.getFacultyId());

        if (subjectDTO.getFacultyName() != null)
            subject.setFacultyName(subjectDTO.getFacultyName());

        if (subjectDTO.getCredits() != null)
            subject.setCredits(subjectDTO.getCredits());

        subject.setUpdatedBy(updatedBy);
        subject.setUpdatedAt(LocalDateTime.now());

        return modelMapper.map(subjectRepository.save(subject), SubjectDTO.class);
    }

    /**
     * Delete subject (soft delete)
     */
    public ApiResponse deleteSubject(String subjectCode, String deletedBy) {

        SubjectEntity subject = subjectRepository.findBySubjectCode(subjectCode)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + subjectCode));

        subject.setActive(false);
        subject.setUpdatedBy(deletedBy);
        subject.setUpdatedAt(LocalDateTime.now());

        subjectRepository.save(subject);

        return new ApiResponse(
                "Subject deleted",
                200,
                null,
                LocalDateTime.now()
        );
    }
    /**
     * Assign faculty to subject
     */
    public ApiResponse assignFacultyToSubject(String subjectCode, String facultyId, String facultyName) {

        SubjectEntity subject = subjectRepository.findBySubjectCode(subjectCode)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + subjectCode));

        subject.setFacultyId(facultyId);
        subject.setFacultyName(facultyName);
        subject.setUpdatedAt(LocalDateTime.now());

        subjectRepository.save(subject);

        return new ApiResponse(
                "Faculty assigned successfully",
                200,
                null,
                LocalDateTime.now()
        );
    }

    /**
     * Update student enrollment count
     */
    public void updateStudentCount(String subjectCode, Integer count) {
        log.debug("Updating student count for subject: {}", subjectCode);

        SubjectEntity subject = subjectRepository.findBySubjectCode(subjectCode).orElse(null);

        if (subject != null) {
            subject.setTotalStudentsEnrolled(count);
            subjectRepository.save(subject);
        }
    }
    private String generateSubjectCode(String departmentId, String subjectName) {

        String cleanDept = departmentId.trim().replaceAll("\\s+", "");
        String cleanName = subjectName.trim().replaceAll("\\s+", "");

        String deptPrefix = cleanDept.length() >= 3
                ? cleanDept.substring(0, 3).toUpperCase()
                : cleanDept.toUpperCase();

        String subPrefix = cleanName.length() >= 3
                ? cleanName.substring(0, 3).toUpperCase()
                : cleanName.toUpperCase();

        String prefix = deptPrefix + subPrefix;

        long count = subjectRepository.countByPrefix(prefix) + 1;
        String number = String.format("%05d", count);

        return prefix + number;
    }

    public ApiResponse getSubjectsByStudentUniversityId(String universityId) {
        log.debug("Fetching subjects for student universityId: {}", universityId);
        List<SubjectDTO> list = subjectRepository.findByStudentUniversityId(universityId)
                .stream()
                .map(subject -> modelMapper.map(subject, SubjectDTO.class))
                .toList();

        return new ApiResponse(
                "Subjects fetched",
                200,
                list,
                LocalDateTime.now()
        );
    }
}