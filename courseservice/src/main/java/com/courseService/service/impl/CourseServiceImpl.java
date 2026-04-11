package com.courseService.service;

import com.courseService.entity.Course;
import com.courseService.exception.ResourceNotFoundException;
import com.courseService.repository.CourseRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository repository;

    //  CREATE - DTO to Entity conversion
    public Course createCourse(@Valid Course dto) {
        log.info("Creating course: {}", dto.getName());

        Course course = new Course();
        course.setName(dto.getName());
        course.setCode(dto.getCode());
        course.setFacultyId(dto.getFacultyId());

        return repository.save(course);
    }

    //  READ ALL
    public List<Course> getAllCourses() {
        log.info("Fetching all courses");
        return repository.findAll();
    }

    //  READ BY ID
    public Course getCourseById(Long id) {
        log.info("Fetching course with ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    //  UPDATE - Consistent DTO usage
    public Course updateCourse(Long id, @Valid Course dto) {
        log.info("Updating course with ID: {}", id);

        Course existing = getCourseById(id);
        existing.setName(dto.getName());
        existing.setCode(dto.getCode());
        existing.setFacultyId(dto.getFacultyId());

        return repository.save(existing);
    }

    //  DELETE
    public void deleteCourse(Long id) {
        log.info("Deleting course with ID: {}", id);
        Course existing = getCourseById(id);  // validates existence
        repository.delete(existing);
    }
}