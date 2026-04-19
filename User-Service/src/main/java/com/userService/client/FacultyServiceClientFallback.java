package com.userService.client;


import com.userService.dto.ApiResponse;
import com.userService.dto.FacultyDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for FacultyServiceClient
 */
@Component
@Slf4j
public class FacultyServiceClientFallback implements FacultyServiceClient {

    @Override
    public ResponseEntity<ApiResponse> createFacultyFromUser(FacultyDTO dto) {
        log.error("Faculty-Service DOWN (CREATE)");
        throw new RuntimeException("Faculty service unavailable");
    }

    @Override
    public ResponseEntity<ApiResponse> deleteFaculty(String universityId) {
        log.error("Faculty-Service DOWN (DELETE)");
        throw new RuntimeException("Faculty service unavailable");
    }

    @Override
    public ResponseEntity<ApiResponse> updateFaculty(String facultyUniversityId, FacultyDTO dto) {
        log.error("Faculty-Service DOWN (UPDATE)");
        throw new RuntimeException("Faculty service unavailable");
    }
}