package com.courseService.service;

import com.courseService.dto.ApiResponse;
import com.courseService.dto.CourseRequestDto;
import com.courseService.entity.Course;
import jakarta.validation.Valid;

import java.util.List;

public interface CourseService {
    // CREATE

     ApiResponse createCourse(@Valid CourseRequestDto dto) ;


     List<Course> getAllCourses();
    // READ BY ID

     ApiResponse getCourseById(Long id);
    // UPDATE

    ApiResponse updateCourse(String code, CourseRequestDto dto) ;

    // DELETE
    ApiResponse deleteCourse(String code);


     ApiResponse getCoursesByFaculty(String facultyUniversityId) ;

     long getTotalCoursesCount();
    ApiResponse assignCoursesToStudent(String universityId, List<String> courseCodes);

    ApiResponse assignCoursesToFacultyByCode(String facultyUniversityId, List<String> courseCodes);
}