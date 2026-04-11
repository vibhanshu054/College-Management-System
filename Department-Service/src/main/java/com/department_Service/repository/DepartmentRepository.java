package com.college.department_Service.repository;



import com.college.department_Service.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long> {

    Optional<DepartmentEntity> findByDepartmentCode(String code);

    Optional<DepartmentEntity> findByDepartmentName(String name);

    @Query("SELECT d FROM DepartmentEntity d WHERE d.active = true")
    List<DepartmentEntity> findAllActive();

    @Query("SELECT COUNT(d) FROM DepartmentEntity d WHERE d.active = true")
    long countActiveOnly();
}