package com.aihealthcare.repository;

import com.aihealthcare.model.entity.Doctor;
import com.aihealthcare.model.enums.DoctorApprovalStatus;
import com.aihealthcare.model.entity.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findBySpecialization(Specialization specialization);
    List<Doctor> findBySpecializationNameContainingIgnoreCase(String name);
    List<Doctor> findByApprovalStatus(DoctorApprovalStatus status);
    Optional<Doctor> findByUserId(Long userId);
    Optional<Doctor> findByEmail(String email);
    long countByApprovalStatus(DoctorApprovalStatus status);
}
