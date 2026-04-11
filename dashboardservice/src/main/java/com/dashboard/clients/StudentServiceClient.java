package com.collage.dashboard.clients;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;

@FeignClient(name = "student-service", url = "http://localhost:8086")
public interface StudentServiceClient {

    @GetMapping("/api/students")
    List<?> getAllStudents();

    @GetMapping("/api/students/count/total")
    Map<String, Long> getTotalStudentsCount();
}
