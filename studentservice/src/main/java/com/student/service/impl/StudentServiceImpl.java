package com.student.service.impl;

import com.student.dto.StudentDTO;
import com.student.entity.AttendanceRecord;
import com.student.entity.StudentEntity;
import com.student.enums.AttendanceStatus;
import com.student.exception.DuplicateStudentException;
import com.student.exception.ResourceNotFoundException;
import com.student.repository.AttendanceRepository;
import com.student.repository.StudentRepository;
import com.student.service.StudentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentServiceImpl implements StudentService {

    private static final String SYSTEM_USER = "SYSTEM";

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final ModelMapper modelMapper;

    @Override
    public Long createStudent(StudentDTO studentDto) {
        log.info("Creating student with email: {}", studentDto.getStudentEmail());

        if (studentRepository.findByStudentEmail(studentDto.getStudentEmail()).isPresent()) {
            log.warn("Duplicate student email: {}", studentDto.getStudentEmail());
            throw new DuplicateStudentException("Student email already exists: " + studentDto.getStudentEmail());
        }

        if (studentRepository.findByUniversityId(studentDto.getUniversityId()).isPresent()) {
            log.warn("Duplicate university ID: {}", studentDto.getUniversityId());
            throw new DuplicateStudentException("University ID already exists: " + studentDto.getUniversityId());
        }

        StudentEntity student = modelMapper.map(studentDto, StudentEntity.class);
        student.setCreatedBy(SYSTEM_USER);
        student.setUpdatedBy(SYSTEM_USER);
        student.setActive(true);
        student.setBooksIssued(0);
        student.setBooksReturned(0);
        student.setAttendancePercentage(0.0f);
        student.setCreatedAt(LocalDateTime.now());
        student.setUpdatedAt(LocalDateTime.now());

        StudentEntity savedStudent = studentRepository.save(student);
        log.info("Student created successfully with ID: {}", savedStudent.getId());

        return savedStudent.getId();
    }

    @Override
    public StudentDTO getStudent(Long id) {
        log.debug("Fetching student with ID: {}", id);
        StudentEntity student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
        return modelMapper.map(student, StudentDTO.class);
    }

    @Override
    public StudentDTO getStudentByUniversityId(String universityId) {
        log.debug("Fetching student with university ID: {}", universityId);
        StudentEntity student = studentRepository.findByUniversityId(universityId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with university ID: " + universityId));
        return modelMapper.map(student, StudentDTO.class);
    }

    @Override
    public List<StudentDTO> getAllStudents(String courseCode, String semester, String department) {
        log.debug("Fetching students with filters - courseCode: {}, semester: {}, dept: {}", courseCode, semester, department);

        List<StudentEntity> students;

        if (courseCode != null && semester != null && department != null) {
            students = studentRepository.findByFilters(courseCode, semester, department);
        } else if (courseCode != null) {
            students = studentRepository.findByCourseCode(courseCode);
        } else if (department != null) {
            students = studentRepository.findByDepartment(department);
        } else {
            students = studentRepository.findByActiveTrue();
        }

        return students.stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public StudentDTO updateStudent(Long id, StudentDTO studentDto) {
        log.info("Updating student with ID: {}", id);

        StudentEntity student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        if (studentDto.getStudentName() != null) {
            student.setStudentName(studentDto.getStudentName());
        }
        if (studentDto.getStudentPhoneNumber() != null) {
            student.setStudentPhoneNumber(studentDto.getStudentPhoneNumber());
        }
        if (studentDto.getSemester() != null) {
            student.setSemester(studentDto.getSemester());
        }
        if (studentDto.getStudentEmail() != null) {
            student.setStudentEmail(studentDto.getStudentEmail());
        }
        if (studentDto.getDepartment() != null) {
            student.setDepartment(studentDto.getDepartment());
        }
        if (studentDto.getCourse() != null) {
            student.setCourse(studentDto.getCourse());
        }
        if (studentDto.getCourseCode() != null) {
            student.setCourseCode(studentDto.getCourseCode());
        }
        if (studentDto.getFacultyId() != null) {
            student.setFacultyId(studentDto.getFacultyId());
        }
        if (studentDto.getFacultyName() != null) {
            student.setFacultyName(studentDto.getFacultyName());
        }
        if (studentDto.getBatch() != null) {
            student.setBatch(studentDto.getBatch());
        }

        student.setUpdatedBy(SYSTEM_USER);
        student.setUpdatedAt(LocalDateTime.now());

        StudentEntity updatedStudent = studentRepository.save(student);
        log.info("Student updated successfully with ID: {}", updatedStudent.getId());

        return modelMapper.map(updatedStudent, StudentDTO.class);
    }

    @Override
    public void deleteStudent(Long id) {
        log.info("Deleting student with ID: {}", id);

        StudentEntity student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        student.setActive(false);
        student.setUpdatedBy(SYSTEM_USER);
        student.setUpdatedAt(LocalDateTime.now());
        studentRepository.save(student);

        log.info("Student deleted successfully with ID: {}", id);
    }

    @Override
    public Map<String, Object> getStudentDashboard(Long id) {
        log.debug("Fetching student dashboard for ID: {}", id);

        StudentEntity student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("profile", modelMapper.map(student, StudentDTO.class));
        dashboard.put("booksIssued", student.getBooksIssued());
        dashboard.put("booksReturned", student.getBooksReturned());
        dashboard.put("attendancePercentage", student.getAttendancePercentage());
        dashboard.put("totalAttendanceRecords", attendanceRepository.countByStudentId(id));

        return dashboard;
    }

    @Override
    public void markAttendance(Long studentId, AttendanceStatus status, Long facultyId, String courseCode) {
        log.info("Marking attendance for student {} with status: {}", studentId, status);

        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        AttendanceRecord record = new AttendanceRecord();
        record.setStudentId(studentId);
        record.setStudentName(student.getStudentName());
        record.setCourseCode(courseCode);
        record.setAttendanceDate(LocalDate.now());
        record.setStatus(status);
        record.setFacultyId(facultyId);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());

        attendanceRepository.save(record);
        log.info("Attendance marked successfully for student: {}", studentId);

        recalculateAttendancePercentage(studentId);
    }

    @Override
    public void recalculateAttendancePercentage(Long studentId) {
        log.debug("Recalculating attendance percentage for student: {}", studentId);

        StudentEntity student = studentRepository.findById(studentId).orElse(null);
        if (student == null) {
            return;
        }

        long totalRecords = attendanceRepository.countByStudentId(studentId);
        if (totalRecords == 0) {
            student.setAttendancePercentage(0.0f);
            student.setUpdatedAt(LocalDateTime.now());
            studentRepository.save(student);
            return;
        }

        long presentCount = attendanceRepository.countByStudentIdAndStatus(studentId, AttendanceStatus.PRESENT);
        float percentage = (float) (presentCount * 100) / totalRecords;

        student.setAttendancePercentage(percentage);
        student.setUpdatedAt(LocalDateTime.now());
        studentRepository.save(student);

        log.debug("Attendance percentage updated to: {}% for student: {}", percentage, studentId);
    }

    @Override
    public List<Map<String, Object>> getAttendanceCalendar(Long id) {
        log.debug("Fetching attendance calendar for student: {}", id);

        List<AttendanceRecord> records = attendanceRepository.findByStudentIdOrderByAttendanceDateDesc(id);

        return records.stream()
                .map(record -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", record.getAttendanceDate());
                    map.put("status", record.getStatus());
                    map.put("courseCode", record.getCourseCode());
                    map.put("facultyId", record.getFacultyId());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void incrementBooksIssued(Long studentId) {
        log.debug("Incrementing books issued for student: {}", studentId);
        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        student.setBooksIssued(student.getBooksIssued() + 1);
        student.setUpdatedAt(LocalDateTime.now());
        studentRepository.save(student);
    }

    @Override
    public void incrementBooksReturned(Long studentId) {
        log.debug("Incrementing books returned for student: {}", studentId);
        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        student.setBooksReturned(student.getBooksReturned() + 1);
        student.setUpdatedAt(LocalDateTime.now());
        studentRepository.save(student);
    }

    @Override
    public List<StudentDTO> getStudentsByCourse(String courseCode) {
        log.debug("Fetching students for course code: {}", courseCode);
        List<StudentEntity> students = studentRepository.findByCourseCode(courseCode);
        return students.stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public long getTotalStudentsCount() {
        log.debug("Getting total students count");
        return studentRepository.count();
    }

    @Override
    public long getStudentsCountByCourse(String courseCode) {
        log.debug("Getting students count for course: {}", courseCode);
        return studentRepository.countByCourseCode(courseCode);
    }

    @Override
    public List<StudentDTO> getStudentsByDepartment(String department) {
        log.debug("Fetching students for department: {}", department);
        List<StudentEntity> students = studentRepository.findByDepartment(department);
        return students.stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public long getStudentsCountByDepartment(String department) {
        log.debug("Getting students count for department: {}", department);
        return studentRepository.countByDepartment(department);
    }

    @Override
    public Map<String, Integer> getBooksStatus(Long id) {
        log.debug("Getting books status for student: {}", id);
        StudentEntity student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        Map<String, Integer> booksStatus = new HashMap<>();
        booksStatus.put("booksIssued", student.getBooksIssued());
        booksStatus.put("booksReturned", student.getBooksReturned());

        return booksStatus;
    }
}