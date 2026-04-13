package com.library.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "studentservice", url = "${student.service.url:http://localhost:8083}")
public interface StudentClient {

    @GetMapping("/api/students/{id}")
    Map<String, Object> getStudentById(@PathVariable Long id);
}