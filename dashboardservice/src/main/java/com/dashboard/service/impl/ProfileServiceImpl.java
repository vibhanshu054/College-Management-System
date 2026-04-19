package com.dashboard.service.impl;

import com.dashboard.clients.FacultyServiceClient;
import com.dashboard.clients.StudentServiceClient;
import com.dashboard.clients.UserServiceClient;
import com.dashboard.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private final StudentServiceClient studentClient;
    private final FacultyServiceClient facultyClient;
    private final UserServiceClient userClient;


    @Override
    public Map<String, Object> getMyProfile(String username, String role, String universityId) {

        log.info("Fetching profile for user {} with role {}", username, role, universityId);

        Map<String, Object> response = new HashMap<>();

        try {

            if ("STUDENT".equalsIgnoreCase(role)) {

                Map<String, Object> student =
                        userClient.getUserByUsername(username);


                response.put("role", "STUDENT");
                response.put("data", studentClient.getStudentByUniversityId(universityId));

            } else if ("FACULTY".equalsIgnoreCase(role)) {

                Map<String, Object> user =
                        userClient.getUserByUsername(username);


                response.put("role", "FACULTY");
                response.put("data", facultyClient.getFacultyByFacultyUniversityId(universityId));

            } else if ("LIBRARIAN".equalsIgnoreCase(role) ||
                    "ADMIN".equalsIgnoreCase(role)) {

                response.put("role", role);
                response.put("data", userClient.getUserByUsername(username));
            }

        } catch (Exception e) {
            log.error("Error fetching profile", e);
            throw new RuntimeException("Profile fetch failed");
        }

        return response;
    }
}