package com.courseService.service.impl;

import com.courseService.dto.CourseRequestDto;
import com.courseService.entity.Course;
import com.courseService.exception.ResourceNotFoundException;
import com.courseService.repository.CourseRepository;
import com.courseService.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository repository;

    // CREATE
    @Override
    public Course createCourse(@Valid CourseRequestDto dto) {
        log.info("Attempting to create course with name: {} and code: {}", dto.getName(), dto.getCode());

        if (repository.existsByCodeOrName(dto.getCode(), dto.getName())) {
            log.warn("Course creation failed. Duplicate found for name: {} or code: {}", dto.getName(), dto.getCode());
            throw new IllegalArgumentException("Course with same code or name already exists");
        }

        Course course = new Course();
        course.setName(dto.getName());
        course.setCode(dto.getCode());

        Course saved = repository.save(course);

        log.info("Course created successfully with ID: {}", saved.getId());
        return saved;
    }

    // READ ALL
    @Override
    public List<Course> getAllCourses() {
        log.info("Fetching all courses");

        List<Course> courses = repository.findAll();

        log.info("Total courses fetched: {}", courses.size());
        return courses;
    }

    // READ BY ID
    @Override
    public Course getCourseById(Long id) {
        log.info("Fetching course with ID: {}", id);

        Course course = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Course not found with ID: {}", id);
                    return new ResourceNotFoundException("Course not found with id: " + id);
                });

        log.info("Course fetched successfully with ID: {}", id);
        return course;
    }

    // UPDATE
    @Override
    public Course updateCourse(Long id, @Valid CourseRequestDto dto) {
        log.info("Attempting to update course with ID: {}", id);

        Course existing = getCourseById(id);

        if (repository.existsByCodeAndIdNot(dto.getCode(), id)) {
            log.warn("Update failed. Duplicate code: {}", dto.getCode());
            throw new IllegalArgumentException("Course code already exists");
        }

        if (repository.existsByNameAndIdNot(dto.getName(), id)) {
            log.warn("Update failed. Duplicate name: {}", dto.getName());
            throw new IllegalArgumentException("Course name already exists");
        }

        existing.setName(dto.getName());
        existing.setCode(dto.getCode());

        Course updated = repository.save(existing);

        log.info("Course updated successfully with ID: {}", updated.getId());
        return updated;
    }

    // DELETE
    @Override
    public void deleteCourse(Long id) {
        log.info("Attempting to delete course with ID: {}", id);

        Course existing = getCourseById(id);

        repository.delete(existing);

        log.info("Course deleted successfully with ID: {}", id);
    }
}