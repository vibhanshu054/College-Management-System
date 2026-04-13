package com.userService.client;


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
    public ResponseEntity<FacultyDTO> createFacultyFromUser(FacultyDTO dto) {
        log.error("Faculty-Service DOWN (CREATE)");
        throw new RuntimeException("Faculty service unavailable");
    }

    @Override
    public ResponseEntity<Void> deleteFaculty(Long id) {
        log.error("Faculty-Service DOWN (DELETE)");
        throw new RuntimeException("Faculty service unavailable");
    }

    @Override
    public ResponseEntity<FacultyDTO> updateFaculty(Long id, FacultyDTO dto) {
        log.error("Faculty-Service DOWN (UPDATE)");
        throw new RuntimeException("Faculty service unavailable");
    }
}