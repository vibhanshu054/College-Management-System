package com.courseService.service.impl;

import com.courseService.dto.ApiResponse;
import com.courseService.dto.CourseRequestDto;
import com.courseService.entity.Course;
import com.courseService.entity.FacultyCourseMapping;
import com.courseService.exception.ResourceNotFoundException;
import com.courseService.repository.CourseRepository;
import com.courseService.repository.FacultyCourseRepository;
import com.courseService.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository repository;
    private final FacultyCourseRepository mappingRepository;

    // CREATE
    @Override
    public ApiResponse createCourse(@Valid CourseRequestDto dto) {
        log.info("Creating course {}", dto.getName());

        if (dto == null || dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Course name is required");
        }

        String code = generateCourseCode(dto.getName());

        // Fix: Use actual entity field names - courseCode, courseName
        if (repository.existsByCourseCodeOrCourseName(code, dto.getName().trim())) {
            throw new IllegalArgumentException("Course already exists");
        }

        Course course = new Course();
        course.setCourseName(dto.getName().trim());
        course.setCourseCode(code);

        Course saved = repository.save(course);

        return new ApiResponse("Course created", 201, saved, LocalDateTime.now());
    }

    @Override
    @Transactional
    public ApiResponse assignCoursesToFacultyByCode(String facultyUniversityId, List<String> courseCodes) {

        if (facultyUniversityId == null || facultyUniversityId.isBlank()) {
            throw new IllegalArgumentException("Faculty universityId is required");
        }

        if (courseCodes == null || courseCodes.isEmpty()) {
            throw new IllegalArgumentException("Course codes list cannot be empty");
        }

        int assigned = 0;
        int skipped = 0;

        for (String code : courseCodes) {
            String trimmedCode = code.trim();


            Course course = repository.findByCourseCode(trimmedCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + trimmedCode));


            boolean alreadyAssigned = mappingRepository.findByFacultyUniversityId(facultyUniversityId)
                    .stream()
                    .anyMatch(mapping -> mapping.getCourseCode().equals(course.getCourseCode()));

            if (alreadyAssigned) {
                skipped++;
                continue;
            }

            FacultyCourseMapping mapping = FacultyCourseMapping.builder()
                    .facultyUniversityId(facultyUniversityId)
                    .courseCode(course.getCourseCode())  // Fix: Use courseCode not courseId
                    .courseName(course.getCourseName())  // Fix: Use courseName not courseId
                    .build();

            mappingRepository.save(mapping);
            assigned++;
        }

        return new ApiResponse(
                "Courses assigned",
                200,
                Map.of("assigned", assigned, "skipped", skipped),
                LocalDateTime.now()
        );
    }

    @Override
    public ApiResponse assignCoursesToStudent(String universityId, List<String> courseCodes) {

        if (universityId == null || courseCodes == null || courseCodes.isEmpty()) {
            throw new IllegalArgumentException("Invalid request");
        }

        for (String code : courseCodes) {
            // Fix: Use courseCode
            repository.findByCourseCode(code.trim())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + code));
        }

        return new ApiResponse(
                "Courses validated for student. Mapping implementation pending",
                200,
                Map.of("studentId", universityId, "courseCodes", courseCodes),
                LocalDateTime.now()
        );
    }

    // READ ALL
    @Override
    public List<Course> getAllCourses() {
        return repository.findAll();
    }

    // READ BY ID
    @Override
    public ApiResponse getCourseById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Course id required");
        }

        Course course = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + id));

        return new ApiResponse(
                "Course fetched",
                200,
                course,
                LocalDateTime.now()
        );
    }

    // UPDATE
    @Override
    @Transactional
    public ApiResponse updateCourse(String code, CourseRequestDto dto) {

        if (dto == null || dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Course name is required");
        }

        // Fix: Use courseCode field name
        Course existing = repository.findByCourseCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        // Fix: Use courseName field name
        if (repository.existsByCourseNameAndIdNot(dto.getName().trim(), existing.getId())) {
            throw new IllegalArgumentException("Duplicate course name");
        }

        existing.setCourseName(dto.getName().trim());

        return new ApiResponse(
                "Course updated",
                200,
                repository.save(existing),
                LocalDateTime.now()
        );
    }

    // DELETE
    @Override
    @Transactional
    public ApiResponse deleteCourse(String code) {

        // Fix: Use courseCode
        Course course = repository.findByCourseCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        // Fix 3: Delete mappings by courseCode (courseId doesn't exist)
        List<FacultyCourseMapping> mappingsToDelete = mappingRepository.findAll().stream()
                .filter(mapping -> mapping.getCourseCode().equals(course.getCourseCode()))
                .toList();

        mappingRepository.deleteAll(mappingsToDelete);
        repository.delete(course);

        return new ApiResponse("Course deleted", 200, null, LocalDateTime.now());
    }

    @Override
    public long getTotalCoursesCount() {
        return repository.count();
    }

    @Override
    public ApiResponse getCoursesByFaculty(String facultyUniversityId) {

        if (facultyUniversityId == null || facultyUniversityId.isBlank()) {
            throw new IllegalArgumentException("Faculty universityId is required");
        }

        List<FacultyCourseMapping> mappings = mappingRepository.findByFacultyUniversityId(facultyUniversityId);

        if (mappings.isEmpty()) {
            throw new ResourceNotFoundException("No courses assigned to this faculty");
        }

        // Fix 4: No course lookup needed, mapping has courseName/courseCode
        List<Map<String, Object>> result = mappings.stream()
                .map(m -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("courseId", null); // courseId doesn't exist
                    map.put("courseName", m.getCourseName());  // Fix: Use mapping data
                    map.put("courseCode", m.getCourseCode());  // Fix: Use mapping data
                    return map;
                })
                .toList();

        return new ApiResponse(
                "Faculty courses fetched successfully",
                200,
                result,
                LocalDateTime.now()
        );
    }

    private String generateCourseCode(String courseName) {
        String cleanedName = courseName.replaceAll("\\s+", "").toUpperCase();
        String prefix = cleanedName.substring(0, Math.min(3, cleanedName.length()));

        // Fix 5: Use courseCode field name
        long count = repository.findAll().stream()
                .filter(c -> c.getCourseCode() != null && c.getCourseCode().startsWith(prefix))
                .count();

        int nextNumber = (int) count + 1;
        return prefix + String.format("%03d", nextNumber);
    }
}