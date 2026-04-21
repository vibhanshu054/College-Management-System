package com.courseService.repository;

import com.courseService.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCourseCode(String code);

    boolean existsByCourseNameAndIdNot(String trim, Long id);

    boolean existsByCourseCodeOrCourseName(String code, String trim);
}