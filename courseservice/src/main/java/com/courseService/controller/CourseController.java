package com.collage.courseservice.controller;

import com.collage.courseservice.entity.Course;
import com.collage.courseservice.service.CourseService;
import com.collage.courseservice.validation.CourseValidation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService service;

    @Autowired
    private CourseValidation courseValidation;

    @PostMapping
    public Course createCourse(@Valid @RequestBody Course course) {
        List<String> errors = courseValidation.validate(course);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errors));
        }
        return service.createCourse(course);
    }

    @GetMapping
    public List<Course> getAllCourses() {
        return service.getAllCourses();
    }

    @GetMapping("/{id}")
    public Course getCourse(@PathVariable Long id) {
        return service.getCourseById(id);
    }

    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable Long id, @Valid @RequestBody Course course) {
        List<String> errors = courseValidation.validate(course);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errors));
        }
        return service.updateCourse(id, course);
    }

    @DeleteMapping("/{id}")
    public String deleteCourse(@PathVariable Long id) {
        service.deleteCourse(id);
        return "Course deleted";
    }
}