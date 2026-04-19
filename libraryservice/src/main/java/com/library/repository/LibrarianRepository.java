package com.library.repository;

import com.library.entity.LibrarianEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LibrarianRepository extends JpaRepository<LibrarianEntity, Long> {

    Optional<LibrarianEntity> findByUniversityId(String universityId);

    Optional<LibrarianEntity> findByLibrarianEmail(String librarianEmail);

}