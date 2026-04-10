package com.collage.student.service;


import com.collage.student.dto.StudentDTO;
import com.collage.student.entity.AttendanceRecord;
import com.collage.student.entity.StudentEntity;
import com.collage.student.enums.AttendanceStatus;
import com.collage.student.exception.DuplicateStudentException;
import com.collage.student.exception.ResourceNotFoundException;
import com.collage.student.repository.AttendanceRepository;
import com.collage.student.repository.StudentRepository;
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
public class StudentService {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final ModelMapper modelMapper;

    /**
     * Create student (from User Service cascade)
     */
    public Long createStudent(StudentDTO StudentDto) {
        log.info("Creating student with email: {}", StudentDto.getStudentEmail());

        // Check if student already exists
        if (studentRepository.findByStudentEmail(StudentDto.getStudentEmail()).isPresent()) {
            log.warn("Duplicate student email: {}", StudentDto.getStudentEmail());
            throw new DuplicateStudentException("Student email already exists: " + StudentDto.getStudentEmail());
        }

        if (studentRepository.findByUniversityId(StudentDto.getUniversityId()).isPresent()) {
            log.warn("Duplicate university ID: {}", StudentDto.getUniversityId());
            throw new DuplicateStudentException("University ID already exists: " + StudentDto.getUniversityId());
        }

        StudentEntity student = modelMapper.map(StudentDto, StudentEntity.class);
        student.setCreatedBy(createdBy);
        student.setUpdatedBy(createdBy);
        student.setActive(true);
        student.setBooksIssued(0);
        student.setBooksReturned(0);
        student.setAttendancePercentage(0.0f);

        StudentEntity savedStudent = studentRepository.save(student);
        log.info("Student created successfully with ID: {}", savedStudent.getId());

        return savedStudent.getId();
    }

    /**
     * Get student by ID
     */
    public StudentDTO getStudent(Long id) {
        log.debug("Fetching student with ID: {}", id);
        StudentEntity student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
        return modelMapper.map(student, StudentDTO.class);
    }

    /**
     * Get student by University ID
     */
    public StudentDTO getStudentByUniversityId(String universityId) {
        log.debug("Fetching student with university ID: {}", universityId);
        StudentEntity student = studentRepository.findByUniversityId(universityId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with university ID: " + universityId));
        return modelMapper.map(student, StudentDTO.class);
    }

    /**
     * Get all students with filters
     */
    public List<StudentDTO> getAllStudents(String courseCode, String semester, String department) {
        log.debug("Fetching students with filters - course: {}, semester: {}, dept: {}", courseCode, semester, department);

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

    /**
     * Update student
     */
    public StudentDTO updateStudent(Long id, StudentDTO StudentDto) {
        log.info("Updating student with ID: {}", id);

        StudentEntity student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        // Update allowed fields
        if (StudentDto.getStudentName() != null) student.setStudentName(StudentDto.getStudentName());
        if (StudentDto.getStudentPhoneNumber() != null)
            student.setStudentPhoneNumber(StudentDto.getStudentPhoneNumber());
        if (StudentDto.getSemester() != null) student.setSemester(StudentDto.getSemester());
        if (StudentDto.getStudentEmail() != null) student.setStudentEmail(StudentDto.getStudentEmail());

        student.setUpdatedBy(updatedBy);
        student.setUpdatedAt(LocalDateTime.now());

        StudentEntity updatedStudent = studentRepository.save(student);
        log.info("Student updated successfully with ID: {}", updatedStudent.getId());

        return modelMapper.map(updatedStudent, StudentDTO.class);
    }

    /**
     * Delete student (soft delete)
     */
    public void deleteStudent(Long id) {
        log.info("Deleting student with ID: {}", id);

        StudentEntity student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        student.setActive(false);
        student.setUpdatedBy(deletedBy);
        student.setUpdatedAt(LocalDateTime.now());
        studentRepository.save(student);

        log.info("Student deleted successfully with ID: {}", id);
    }

    /**
     * Get student dashboard
     */
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

    /**
     * Mark attendance
     */
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

        attendanceRepository.save(record);
        log.info("Attendance marked successfully for student: {}", studentId);

        // Recalculate attendance percentage
        recalculateAttendancePercentage(studentId);
    }

    /**
     * Recalculate attendance percentage
     */
    private void recalculateAttendancePercentage(Long studentId) {
        log.debug("Recalculating attendance percentage for student: {}", studentId);

        StudentEntity student = studentRepository.findById(studentId).orElse(null);
        if (student == null) return;

        long totalRecords = attendanceRepository.countByStudentId(studentId);
        if (totalRecords == 0) {
            student.setAttendancePercentage(0.0f);
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

    /**
     * Get student attendance calendar
     */
    public List<Map<String, Object>> getAttendanceCalendar(Long id) {
        log.debug("Fetching attendance calendar for student: {}", id);

        List<AttendanceRecord> records = attendanceRepository.findByStudentIdOrderByAttendanceDateDesc(id);

        return records.stream()
                .map(record -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", record.getAttendanceDate());
                    map.put("status", record.getStatus());
                    map.put("courseCode", record.getCourseCode());
                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * Update book count
     */
    public void incrementBooksIssued(Long studentId) {
        log.debug("Incrementing books issued for student: {}", studentId);
        StudentEntity student = studentRepository.findById(studentId).orElse(null);
        if (student != null) {
            student.setBooksIssued(student.getBooksIssued() + 1);
            studentRepository.save(student);
        }
    }

    public void incrementBooksReturned(Long studentId) {
        log.debug("Incrementing books returned for student: {}", studentId);
        StudentEntity student = studentRepository.findById(studentId).orElse(null);
        if (student != null) {
            student.setBooksReturned(student.getBooksReturned() + 1);
            studentRepository.save(student);
        }
    }

    /**
     * Get students by course code
     */
    public List<StudentDTO> getStudentsByCourseCode(String courseCode) {
        log.debug("Fetching students for course code: {}", courseCode);
        List<StudentEntity> students = studentRepository.findByCourseCode(courseCode);
        return students.stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Get total students count
     */
    public long getTotalStudentsCount() {
        log.debug("Getting total students count");
        return studentRepository.count();
    }

    /**
     * Get students count by course
     */
    public long getStudentsCountByCourse(String courseCode) {
        log.debug("Getting students count for course: {}", courseCode);
        return studentRepository.countByCourseCode(courseCode);
    }

    public List<StudentDTO> getStudentsByDepartment(String department) {
        log.debug("Fetching students for department: {}", department);
        List<StudentEntity> students = studentRepository.findByDepartment(department);
        return students.stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());
    }

     public long getStudentsCountByDepartment(String department) {
        log.debug("Getting students count for department: {}", department);
        return studentRepository.countByDepartment(department);
    }

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