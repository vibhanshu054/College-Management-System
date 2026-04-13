package com.dashboard.clients;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name = "COURSE-SERVICE")
public interface CourseServiceClient {

    @GetMapping("/api/courses")
    List<?> getAllCourses();

    @GetMapping("/api/courses/count/total")
    Map<String, Long> getTotalCoursesCount();
}