package com.dashboard.clients;

import com.dashboard.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CourseServiceClientFallback implements CourseServiceClient {

    @Override
    public ResponseEntity<ApiResponse> getAllCourses() {
        log.error("Course-Service DOWN - Fallback triggered for getAllCourses");
        return buildErrorResponse("Course service is unavailable");
    }

    @Override
    public ResponseEntity<ApiResponse> getTotalCoursesCount() {
        log.error("Course-Service DOWN - Fallback triggered for getTotalCoursesCount");
        Map<String, Object> data = new HashMap<>();
        data.put("totalCourses", 0);
        return ResponseEntity.ok(new ApiResponse("Fallback response", 503, data, LocalDateTime.now()));
    }

    @Override
    public ResponseEntity<ApiResponse> getCoursesByFaculty(String universityId) {
        log.error("Course-Service DOWN - Fallback triggered for getCoursesByFaculty: {}", universityId);
        return buildErrorResponse("Course service unavailable for faculty: " + universityId);
    }

    private ResponseEntity<ApiResponse> buildErrorResponse(String message) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ApiResponse(message, 503, new HashMap<>(), LocalDateTime.now()));
    }
}