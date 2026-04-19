package com.dashboard.service;


import com.dashboard.dto.ApiResponse;

import java.util.List;

public interface DashboardService {


    ApiResponse getAdminDashboard();

    ApiResponse getStudentDashboard(String universityId);

    ApiResponse getFacultyDashboard(String facultyUniversityId);


    ApiResponse getLibrarianDashboard();


    ApiResponse getDashboardByRole(String role, String userId);

    double calculateAttendance(List<?> list);

}