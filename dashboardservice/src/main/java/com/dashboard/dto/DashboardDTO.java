package com.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardDTO {

    // Admin Dashboard
    private AdminDashboardDTO adminDashboard;

    // Faculty Dashboard
    private FacultyDashboardDTO facultyDashboard;

    // Student Dashboard
    private StudentDashboardDTO studentDashboard;

    // Librarian Dashboard
    private LibrarianDashboardDTO librarianDashboard;

    private LocalDateTime lastUpdated;
}






