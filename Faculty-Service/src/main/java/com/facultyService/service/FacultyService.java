package com.facultyService.service;


import com.facultyService.dto.FacultyDTO;

import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;

@Service
public interface FacultyService {

    


    FacultyDTO createFaculty(FacultyDTO facultyDTO);

    FacultyDTO getFaculty(Long id);

    FacultyDTO getFacultyByUniversityId(String universityId);

    List<FacultyDTO> getAllFaculty(String department, String subRole);

    FacultyDTO updateFaculty(Long id, FacultyDTO facultyDTO);

    void deleteFaculty(Long id);

    Map<String, Object> getFacultyDashboard(Long id);

    String getAttendanceCalendar(Long id);

    String getSchedule(Long id);

    void updateSchedule(Long id, Map<String, Object> scheduleData);

    List<FacultyDTO> getFacultyByDepartment(String department);

    Integer getTotalFacultyCount();
}