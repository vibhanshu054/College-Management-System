package com.dashboard.service;


import com.dashboard.dto.*;

public interface DashboardService {

    AdminDashboardDTO getAdminDashboard();

    StudentDashboardDTO getStudentDashboard(String studentId);

    FacultyDashboardDTO getFacultyDashboard(String facultyId);

    LibrarianDashboardDTO getLibrarianDashboard();

    DashboardDTO getDashboardByRole(String role, String userId);

}