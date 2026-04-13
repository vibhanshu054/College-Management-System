package com.dashboard.clients;

import com.dashboard.dto.SubjectDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "SUBJECT-SERVICE")
public interface SubjectServiceClient {

    @GetMapping("/api/subjects")
    List<SubjectDTO> getAllSubjects();

    @GetMapping("/api/subjects/count/total")
    Map<String, Long> getTotalSubjectsCount();

    //  FIXED (remove static + proper type)
    @GetMapping("/api/subjects/student/{studentId}")
    List<SubjectDTO> getSubjectsByStudent(@PathVariable String studentId);
}