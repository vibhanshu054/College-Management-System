package com.courseService.service;

import com.courseService.dto.CourseRequestDto;
import com.courseService.entity.Course;
import jakarta.validation.Valid;

import java.util.List;

public interface CourseService {
     Course createCourse(@Valid CourseRequestDto dto);

    //  READ ALL
    List<Course> getAllCourses() ;

    //  READ BY ID
     Course getCourseById(Long id);

    //  UPDATE - Consistent DTO usage
     Course updateCourse(Long id, @Valid CourseRequestDto dto);

    //  DELETE
     void deleteCourse(Long id);
}


