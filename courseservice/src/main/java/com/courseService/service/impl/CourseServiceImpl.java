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

        if (repository.existsByCodeOrName(code, dto.getName().trim())) {
            throw new IllegalArgumentException("Course already exists");
        }

        Course course = new Course();
        course.setName(dto.getName().trim());
        course.setCode(code);

        Course saved = repository.save(course);

        return new ApiResponse("Course created", 201, saved, LocalDateTime.now());
    }

    @Override
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

            Course course = repository.findByCode(code.trim())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + code));

            // ✅ correct duplicate check
            if (mappingRepository.existsByFacultyUniversityIdAndCourseId(
                    facultyUniversityId, course.getId())) {
                skipped++;
                continue;
            }

            // ✅ correct mapping
            FacultyCourseMapping mapping = FacultyCourseMapping.builder()
                    .facultyUniversityId(facultyUniversityId)
                    .courseId(course.getId())
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

        // TODO: call STUDENT-SERVICE (Feign Client)
        // Example: Student student = studentClient.getStudentByUniversityId(universityId);

        for (String code : courseCodes) {

            Course course = repository.findByCode(code)
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + code));

            // 👉 yaha tu student-course mapping save karega (alag table hona chahiye)
        }

        return new ApiResponse(
                "Courses assigned to student",
                200,
                null,
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
                java.time.LocalDateTime.now()
        );
    }

    // UPDATE
    @Override

    public ApiResponse updateCourse(String code, CourseRequestDto dto) {

        if (dto == null || dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Course name is required");
        }

        Course existing = repository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (repository.existsByNameAndIdNot(dto.getName().trim(), existing.getId())) {
            throw new IllegalArgumentException("Duplicate course name");
        }

        existing.setName(dto.getName().trim());

        return new ApiResponse(
                "Course updated",
                200,
                repository.save(existing),
                LocalDateTime.now()
        );
    }

    // DELETE
    @Override
    public ApiResponse deleteCourse(String code) {

        Course course = repository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        // delete mappings by courseId
        mappingRepository.deleteAll(
                mappingRepository.findByCourseId(course.getId())
        );

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
            throw new IllegalArgumentException("Faculty facultyUniversityId is required");
        }

        List<FacultyCourseMapping> mappings =
                mappingRepository.findByFacultyUniversityId(facultyUniversityId);

        if (mappings.isEmpty()) {
            throw new ResourceNotFoundException("No courses assigned to this faculty");
        }

        List<Map<String, Object>> result = mappings.stream()
                .map(m -> {
                    Course course = repository.findById(m.getCourseId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Course not found for id: " + m.getCourseId()
                            ));

                    Map<String, Object> map = new HashMap<>();
                    map.put("courseId", course.getId());
                    map.put("courseName", course.getName());
                    map.put("courseCode", course.getCode());

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

        long count = repository.findAll().stream()
                .filter(c -> c.getCode() != null && c.getCode().startsWith(prefix))
                .count();

        int nextNumber = (int) count + 1;

        return prefix + String.format("%03d", nextNumber);
    }

}