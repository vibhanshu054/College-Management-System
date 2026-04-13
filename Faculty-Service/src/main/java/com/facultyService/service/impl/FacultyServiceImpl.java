package com.facultyService.service.impl;

import com.facultyService.dto.FacultyDTO;
import com.facultyService.entity.FacultyEntity;
import com.facultyService.enums.FacultySubRole;
import com.facultyService.repository.FacultyRepository;
import com.facultyService.service.FacultyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository facultyRepository;
    private final ModelMapper modelMapper;

    @Override
    public FacultyDTO createFaculty(FacultyDTO facultyDTO) {
        log.info("Creating faculty with universityId: {}", facultyDTO.getUniversityId());

        facultyRepository.findByUniversityId(facultyDTO.getUniversityId())
                .ifPresent(f -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Faculty already exists with universityId: " + facultyDTO.getUniversityId());
                });

        facultyRepository.findByFacultyEmail(facultyDTO.getFacultyEmail())
                .ifPresent(f -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Faculty already exists with email: " + facultyDTO.getFacultyEmail());
                });

        FacultyEntity faculty = modelMapper.map(facultyDTO, FacultyEntity.class);
        FacultyEntity savedFaculty = facultyRepository.save(faculty);

        return modelMapper.map(savedFaculty, FacultyDTO.class);
    }

    @Override
    public FacultyDTO getFaculty(Long id) {
        log.info("Fetching faculty by id: {}", id);

        FacultyEntity faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Faculty not found with id: " + id));

        return modelMapper.map(faculty, FacultyDTO.class);
    }

    @Override
    public FacultyDTO getFacultyByUniversityId(String universityId) {
        log.info("Fetching faculty by universityId: {}", universityId);

        FacultyEntity faculty = facultyRepository.findByUniversityId(universityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Faculty not found with universityId: " + universityId));

        return modelMapper.map(faculty, FacultyDTO.class);
    }

    @Override
    public List<FacultyDTO> getAllFaculty(String department, String subRole) {
        log.info("Fetching all faculty with department: {} and subRole: {}", department, subRole);

        List<FacultyEntity> facultyList;

        if (department != null && !department.isBlank() && subRole != null && !subRole.isBlank()) {
            FacultySubRole role = FacultySubRole.valueOf(subRole.toUpperCase());
            facultyList = facultyRepository.findByDepartmentIgnoreCaseAndSubRole(department, role);
        } else if (department != null && !department.isBlank()) {
            facultyList = facultyRepository.findByDepartmentIgnoreCase(department);
        } else if (subRole != null && !subRole.isBlank()) {
            FacultySubRole role = FacultySubRole.valueOf(subRole.toUpperCase());
            facultyList = facultyRepository.findBySubRole(role);
        } else {
            facultyList = facultyRepository.findAll();
        }

        return modelMapper.map(facultyList, new TypeToken<List<FacultyDTO>>(){}.getType());
    }

    @Override
    public FacultyDTO updateFaculty(Long id, FacultyDTO facultyDTO) {
        log.info("Updating faculty with id: {}", id);

        FacultyEntity faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Faculty not found with id: " + id));

        if (facultyDTO.getFacultyEmail() != null
                && !facultyDTO.getFacultyEmail().equalsIgnoreCase(faculty.getFacultyEmail())) {
            facultyRepository.findByFacultyEmail(facultyDTO.getFacultyEmail())
                    .ifPresent(existing -> {
                        throw new ResponseStatusException(HttpStatus.CONFLICT,
                                "Faculty already exists with email: " + facultyDTO.getFacultyEmail());
                    });
        }

        modelMapper.map(facultyDTO, faculty);
        faculty.setId(id);
        faculty.setUniversityId(faculty.getUniversityId());

        FacultyEntity updatedFaculty = facultyRepository.save(faculty);
        return modelMapper.map(updatedFaculty, FacultyDTO.class);
    }

    @Override
    public void deleteFaculty(Long id) {
        log.info("Deleting faculty with id: {}", id);

        FacultyEntity faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Faculty not found with id: " + id));

        facultyRepository.delete(faculty);
    }

    @Override
    public Map<String, Object> getFacultyDashboard(Long id) {
        log.info("Fetching dashboard for faculty id: {}", id);

        FacultyEntity faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Faculty not found with id: " + id));

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("faculty", modelMapper.map(faculty, FacultyDTO.class));
        dashboard.put("booksIssued", faculty.getBooksIssued() != null ? faculty.getBooksIssued() : 0);
        dashboard.put("booksReturned", faculty.getBooksReturned() != null ? faculty.getBooksReturned() : 0);
        dashboard.put("todaySchedule", faculty.getScheduleData() != null ? faculty.getScheduleData() : "No schedule available");
        dashboard.put("attendancePercentage", faculty.getAttendancePercentage() != null ? faculty.getAttendancePercentage() : 0.0f);

        return dashboard;
    }

    @Override
    public String getAttendanceCalendar(Long id) {
        log.info("Fetching attendance calendar for faculty id: {}", id);

        FacultyEntity faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Faculty not found with id: " + id));

        return faculty.getAttendanceCalendar() != null ? faculty.getAttendanceCalendar() : "No attendance data available";
    }

    @Override
    public String getSchedule(Long id) {
        log.info("Fetching schedule for faculty id: {}", id);

        FacultyEntity faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Faculty not found with id: " + id));

        return faculty.getScheduleData() != null ? faculty.getScheduleData() : "No schedule assigned";
    }

    @Override
    public void updateSchedule(Long id, Map<String, Object> scheduleData) {
        log.info("Updating schedule for faculty id: {}", id);

        FacultyEntity faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Faculty not found with id: " + id));

        Object schedule = scheduleData.get("schedule");
        faculty.setScheduleData(schedule != null ? schedule.toString() : null);

        facultyRepository.save(faculty);
    }

    @Override
    public List<FacultyDTO> getFacultyByDepartment(String department) {
        log.info("Fetching faculty by department: {}", department);

        return facultyRepository.findByDepartmentIgnoreCase(department)
                .stream()
                .map(faculty -> modelMapper.map(faculty, FacultyDTO.class))
                .toList();
    }

    @Override
    public Integer getTotalFacultyCount() {
        log.info("Fetching total active faculty count");
        Integer count = facultyRepository.countByActiveTrue();
        return count != null ? count : 0;
    }

    private FacultySubRole parseSubRole(String subRole) {
        try {
            return FacultySubRole.valueOf(subRole.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid faculty subRole: " + subRole);
        }
    }
}