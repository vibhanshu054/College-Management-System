package com.authService.repository;


import com.authService.entity.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository  extends JpaRepository<OtpEntity, Long> {
    Optional<OtpEntity> findByToken(String token);
}
