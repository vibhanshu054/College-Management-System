package com.userService.client;



import com.userService.dto.StudentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for StudentServiceClient
 */
@Component
@Slf4j
public class StudentServiceClientFallback implements StudentServiceClient {

    @Override
    public ResponseEntity<StudentDTO> createStudentFromUser(StudentDTO dto) {
        log.error("Student-Service DOWN (CREATE)");
        throw new RuntimeException("Student service unavailable");
    }

    @Override
    public ResponseEntity<Void> deleteStudent(String universityId) {
        log.error("Student-Service DOWN (DELETE)");
        throw new RuntimeException("Student service unavailable");
    }

    @Override
    public ResponseEntity<StudentDTO> updateStudent(String universityId, StudentDTO dto) {
        log.error("Student-Service DOWN (UPDATE)");
        throw new RuntimeException("Student service unavailable");
    }
}