package com.userService.services.impl;

import com.userService.client.FacultyServiceClient;
import com.userService.client.LibraryServiceClient;
import com.userService.client.StudentServiceClient;
import com.userService.dto.FacultyDTO;
import com.userService.dto.LibrarianDTO;
import com.userService.dto.StudentDTO;
import com.userService.dto.UserDto;
import com.userService.entity.UserEntity;
import com.userService.enums.FacultySubRole;
import com.userService.enums.UserRole;
import com.userService.exception.DuplicateResourceException;
import com.userService.exception.InvalidOperationException;
import com.userService.exception.ResourceNotFoundException;
import com.userService.repository.FacultyRepository;
import com.userService.repository.LibrarianRepository;
import com.userService.repository.StudentRepository;
import com.userService.repository.UserRepository;
import com.userService.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FacultyRepository facultyRepository;
    private final LibrarianRepository librarianRepository;
    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final StudentServiceClient studentServiceClient;
    private final FacultyServiceClient facultyServiceClient;
    private final LibraryServiceClient libraryServiceClient;

    // =========================================================
    // CREATE USER
    // =========================================================
    @Override
    public UserDto createUser(UserDto dto, String createdBy) {

        log.info("CREATE USER | email={} | role={}", dto.getEmail(), dto.getRole());

        validate(dto);
        checkDuplicate(dto);

        UserEntity user = buildUser(dto, createdBy);

        user = userRepository.save(user);

        log.info("USER SAVED | id={} | email={}", user.getId(), user.getEmail());

        cascadeCreate(user);

        return modelMapper.map(user, UserDto.class);
    }

    // =========================================================
    // VALIDATION
    // =========================================================
    private void validate(UserDto dto) {

        if (dto == null)
            throw new InvalidOperationException("User data cannot be null");

        if (dto.getEmail() == null || dto.getEmail().isBlank())
            throw new InvalidOperationException("Email is required");

        if (dto.getUsername() == null || dto.getUsername().isBlank())
            throw new InvalidOperationException("Username is required");

        if (dto.getPassword() == null || dto.getPassword().length() < 8)
            throw new InvalidOperationException("Password must be at least 8 characters");

        if (dto.getRole() == null || dto.getRole().isBlank())
            throw new InvalidOperationException("Role is required");
    }

    // =========================================================
    // DUPLICATE CHECK
    // =========================================================
    private void checkDuplicate(UserDto dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent())
            throw new DuplicateResourceException("Email already exists");

        if (dto.getUniversityId() != null &&
                userRepository.findByUniversityId(dto.getUniversityId()).isPresent())
            throw new DuplicateResourceException("University ID already exists");
    }

    // =========================================================
    // BUILD USER
    // =========================================================
    private UserEntity buildUser(UserDto dto, String createdBy) {

        return UserEntity.builder()
                .email(dto.getEmail())
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(parseRole(dto.getRole()))
                .department(dto.getDepartment())
                .phoneNumber(dto.getPhoneNumber())
                .universityId(dto.getUniversityId())
                .semester(dto.getSemester())
                .batch(dto.getBatch())
                .courseCode(dto.getCourseCode())
                .facultySubRole(parseFaculty(dto.getFacultySubRole()))
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(createdBy)
                .updatedBy(createdBy)
                .build();
    }

    private UserRole parseRole(String role) {
        try {
            return UserRole.valueOf(role.trim().toUpperCase());
        } catch (Exception e) {
            throw new InvalidOperationException("Invalid role: " + role);
        }
    }

    private FacultySubRole parseFaculty(String role) {
        if (role == null || role.isBlank()) return null;
        return FacultySubRole.valueOf(role.trim().toUpperCase());
    }

    // =========================================================
    // CASCADE CREATE (FIXED)
    // =========================================================
    private void cascadeCreate(UserEntity user) {

        try {
            switch (user.getRole()) {

                case STUDENT -> {
                    StudentDTO dto = StudentDTO.builder()
                            .studentName(user.getUsername())
                            .studentEmail(user.getEmail())
                            .studentPhoneNumber(user.getPhoneNumber())
                            .universityId(user.getUniversityId())
                            .semester(user.getSemester())
                            .batch(user.getBatch())
                            .department(user.getDepartment())
                            .courseCode(user.getCourseCode())
                            .active(true)
                            .build();

                    var res = studentServiceClient.createStudentFromUser(dto);
                    if (res.getBody() != null)
                        user.setStudentServiceId(res.getBody().getId());
                }

                case FACULTY -> {
                    FacultyDTO dto = FacultyDTO.builder()
                            .facultyName(user.getUsername())
                            .facultyEmail(user.getEmail())
                            .facultyPhoneNumber(user.getPhoneNumber())
                            .universityId(user.getUniversityId())
                            .department(user.getDepartment())
                            .subRole(user.getFacultySubRole() != null
                                    ? user.getFacultySubRole().name()
                                    : "TRAINEE")
                            .active(true)
                            .build();

                    var res = facultyServiceClient.createFacultyFromUser(dto);
                    if (res.getBody() != null)
                        user.setFacultyServiceId(res.getBody().getId());
                }

                case LIBRARIAN -> {
                    LibrarianDTO dto = LibrarianDTO.builder()
                            .librarianName(user.getUsername())
                            .librarianEmail(user.getEmail())
                            .librarianPhoneNumber(user.getPhoneNumber())
                            .universityId(user.getUniversityId())
                            .active(true)
                            .build();

                    var res = libraryServiceClient.createLibrarianFromUser(dto);
                    if (res.getBody() != null)
                        user.setLibrarianServiceId(res.getBody().getId());
                }

                case ADMIN -> log.info("ADMIN - no cascade required");
            }

            userRepository.save(user); // IMPORTANT FIX

        } catch (Exception e) {
            log.error("CASCADE CREATE FAILED | email={}", user.getEmail(), e);
        }
    }

    // =========================================================
    // GET METHODS (CLEAN)
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {

        return userRepository.findById(id)
                .map(u -> modelMapper.map(u, UserDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .map(u -> modelMapper.map(u, UserDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByUniversityId(String universityId) {

        return userRepository.findByUniversityId(universityId)
                .map(u -> modelMapper.map(u, UserDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + universityId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {

        return userRepository.findByActiveTrue()
                .stream()
                .map(u -> modelMapper.map(u, UserDto.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsersByRole(String role) {

        UserRole userRole = UserRole.valueOf(role.trim().toUpperCase());

        return userRepository.findByRoleAndActiveTrue(userRole)
                .stream()
                .map(u -> modelMapper.map(u, UserDto.class))
                .toList();
    }

    // =========================================================
    // UPDATE
    // =========================================================
    @Override
    public UserDto updateUser(Long id, UserDto dto, String updatedBy, UserRole role) {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getDepartment() != null) user.setDepartment(dto.getDepartment());

        if (dto.getEmail() != null &&
                !dto.getEmail().equalsIgnoreCase(user.getEmail())) {

            if (userRepository.findByEmail(dto.getEmail()).isPresent())
                throw new DuplicateResourceException("Email already exists");

            user.setEmail(dto.getEmail());
        }

        user.setUpdatedBy(updatedBy);
        user.setUpdatedAt(LocalDateTime.now());
        cascadeUpdate(user);

        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    // =========================================================
    // DELETE
    // =========================================================
    @Override
    public void deleteUser(Long id, String deletedBy) {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        cascadeDelete(user);

        user.setActive(false);
        user.setUpdatedBy(deletedBy);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    // =========================================================
    // PASSWORD
    // =========================================================
    @Override
    public void updatePassword(Long id, String oldPwd, String newPwd, String updatedBy) {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPwd, user.getPassword()))
            throw new InvalidOperationException("Old password incorrect");

        if (newPwd == null || newPwd.length() < 8)
            throw new InvalidOperationException("Weak password");

        user.setPassword(passwordEncoder.encode(newPwd));
        user.setUpdatedBy(updatedBy);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    // =========================================================
    // VERIFY
    // =========================================================
    @Override
    public UserDto verifyCredentials(String email, String password) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isActive())
            throw new InvalidOperationException("Inactive user");

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new InvalidOperationException("Invalid credentials");

        return modelMapper.map(user, UserDto.class);
    }

    // =========================================================
    // CASCADE DELETE
    // =========================================================
    private void cascadeDelete(UserEntity user) {

        try {
            switch (user.getRole()) {

                case STUDENT -> {
                    if (user.getStudentServiceId() != null)
                        studentServiceClient.deleteStudent(user.getStudentServiceId());
                }

                case FACULTY -> {
                    if (user.getFacultyServiceId() != null)
                        facultyServiceClient.deleteFaculty(user.getFacultyServiceId());
                }

                case LIBRARIAN -> {
                    if (user.getLibrarianServiceId() != null)
                        libraryServiceClient.deleteLibrarian(user.getLibrarianServiceId());
                }

                case ADMIN -> log.info("ADMIN delete");
            }

        } catch (Exception e) {
            log.error("CASCADE DELETE FAILED | email={}", user.getEmail(), e);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public long getUserCountByRole(String role) {

        try {
            UserRole userRole = UserRole.valueOf(role.trim().toUpperCase());
            return userRepository.countByRoleAndActiveTrue(userRole);

        } catch (Exception e) {
            throw new InvalidOperationException("Invalid role: " + role);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getUserCountByRoleAndDepartment(String role, String department) {

        if (department == null || department.isBlank()) {
            throw new InvalidOperationException("Department cannot be empty");
        }

        try {
            UserRole userRole = UserRole.valueOf(role.trim().toUpperCase());
            return userRepository.countByRoleAndDepartmentAndActiveTrue(userRole, department);

        } catch (Exception e) {
            throw new InvalidOperationException("Invalid role or department");
        }
    }
    // =========================================================
// CASCADE UPDATE (FINAL FIX)
// =========================================================
    private void cascadeUpdate(UserEntity user) {

        try {
            switch (user.getRole()) {

                case STUDENT -> {
                    if (user.getStudentServiceId() != null) {

                        StudentDTO dto = StudentDTO.builder()
                                .studentName(user.getUsername())
                                .studentEmail(user.getEmail())
                                .studentPhoneNumber(user.getPhoneNumber())
                                .department(user.getDepartment())
                                .semester(user.getSemester())
                                .batch(user.getBatch())
                                .courseCode(user.getCourseCode())
                                .build();

                        studentServiceClient.updateStudent(
                                user.getStudentServiceId(),
                                dto
                        );
                    }
                }

                case FACULTY -> {
                    if (user.getFacultyServiceId() != null) {

                        FacultyDTO dto = FacultyDTO.builder()
                                .facultyName(user.getUsername())
                                .facultyEmail(user.getEmail())
                                .facultyPhoneNumber(user.getPhoneNumber())
                                .department(user.getDepartment())
                                .subRole(user.getFacultySubRole() != null
                                        ? user.getFacultySubRole().name()
                                        : "TRAINEE")
                                .build();

                        facultyServiceClient.updateFaculty(
                                user.getFacultyServiceId(),
                                dto
                        );
                    }
                }

                case LIBRARIAN -> {
                    if (user.getLibrarianServiceId() != null) {

                        LibrarianDTO dto = LibrarianDTO.builder()
                                .librarianName(user.getUsername())
                                .librarianEmail(user.getEmail())
                                .librarianPhoneNumber(user.getPhoneNumber())
                                .build();

                        libraryServiceClient.updateLibrarian(
                                user.getLibrarianServiceId(),
                                dto
                        );
                    }
                }

                case ADMIN -> log.info("ADMIN - no cascade update required");
            }

        } catch (Exception e) {
            log.error("CASCADE FAILED", e);
            throw new RuntimeException("Service sync failed");
        }
    }
}