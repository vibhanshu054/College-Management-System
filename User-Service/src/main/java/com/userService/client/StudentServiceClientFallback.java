package com.userService.client;

import com.userService.dto.ApiResponse;
import com.userService.dto.StudentDTO;
import com.userService.exception.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StudentServiceClientFallback implements StudentServiceClient {

    @Override
    public ResponseEntity<StudentDTO> createStudentFromUser(StudentDTO studentDTO) {
        String errorMsg = "Student-Service is unavailable. Please try again later.";
        log.error("Feign Fallback (CREATE): {} for student email: {}", errorMsg,
                studentDTO != null ? studentDTO.getStudentEmail() : "unknown");
        throw new ServiceUnavailableException(errorMsg + " [CREATE_STUDENT]");
    }

    @Override
    public ResponseEntity<Void> deleteStudent(String universityId) {
        String errorMsg = "Student-Service is unavailable for deletion.";
        log.error("Feign Fallback (DELETE): {} for ID: {}", errorMsg, universityId);
        throw new ServiceUnavailableException(errorMsg + " [DELETE_STUDENT]");
    }

    @Override
    public ResponseEntity<StudentDTO> updateStudent(String universityId, StudentDTO dto) {
        String errorMsg = "Student-Service is unavailable for update.";
        log.error("Feign Fallback (UPDATE): {} for ID: {}", errorMsg, universityId);
        throw new ServiceUnavailableException(errorMsg + " [UPDATE_STUDENT]");
    }

    @Override
    public ResponseEntity<ApiResponse> getStudentByUniversityId(String universityId) {
        String errorMsg = "Student-Service is unavailable for Get.";
        log.error("Feign Fallback (GET): {} for ID: {}", errorMsg, universityId);
        throw new ServiceUnavailableException(errorMsg + " [GET_STUDENT]");
    }
}