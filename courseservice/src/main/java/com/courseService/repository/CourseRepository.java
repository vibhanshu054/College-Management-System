package com.courseService.repository;

import com.courseService.entity.Course;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByCodeOrName(@NotBlank(message = "Course code is required") String code, @NotBlank(message = "Course name is required") String name);
    boolean existsByCodeAndIdNot(String code, Long id);
    boolean existsByNameAndIdNot(String name, Long id);
}