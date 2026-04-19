package com.facultyService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "LIBRARY-SERVICE")
public interface LibraryClient {

    @GetMapping("/api/library/books/faculty/{universityId}")
    Map<String, Object> getFacultyBooks(@PathVariable String universityId);
    @DeleteMapping("/api/library/faculty/{universityId}")
    void removeFacultyRecords(@PathVariable String universityId);
}