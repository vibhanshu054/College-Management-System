package com.collage.courseservice.repository;

import com.collage.courseservice.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CourseRepository extends JpaRepository<Course, Long> {
}