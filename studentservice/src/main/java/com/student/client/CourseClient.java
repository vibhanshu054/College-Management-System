
//Feign client to call Course Service
package com.collage.student.client;

  import org.springframework.cloud.openfeign.FeignClient;
  import org.springframework.web.bind.annotation.GetMapping;
  import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "courseservice", url = "http://localhost:8082")
public interface CourseClient {

    @GetMapping("/api/courses/{id}")
    Object getCourseById(@PathVariable Long id);
}


