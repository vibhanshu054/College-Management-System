package com.courseService.repository;

import com.courseService.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsByCodeOrName(String code, String name);

    boolean existsByNameAndIdNot(String name, Long id);

    Optional<Course> findByCode(String code);
}