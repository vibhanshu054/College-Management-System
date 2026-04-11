package com.collage.courseservice.service;


import com.collage.courseservice.entity.Course;
import com.collage.courseservice.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import com.collage.courseservice.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);

    @Autowired
    private CourseRepository repository;

    public Course createCourse(Course course) {
        logger.info("Creating course: {}", course.getName());
        return repository.save(course);
    }

    public List<Course> getAllCourses() {
        logger.info("Fetching all courses");
        return repository.findAll();
    }

    public Course getCourseById(Long id) {
        logger.info("Fetching course with ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    public Course updateCourse(Long id, Course course) {
        logger.info("Updating course with ID: {}", id);
        Course existing = getCourseById(id);
        existing.setName(course.getName());
        existing.setCode(course.getCode());
        existing.setFacultyId(course.getFacultyId());
        return repository.save(existing);
    }

    public void deleteCourse(Long id) {
        logger.info("Deleting course with ID: {}", id);
        getCourseById(id); // throws 404 if not found
        repository.deleteById(id);
    }
}