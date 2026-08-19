package com.aihealthcare.repository;

import com.aihealthcare.model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByDoctorIdOrderByCreatedAtDesc(Long doctorId);

    long countByDoctorId(Long doctorId);

    boolean existsByUserIdAndAppointmentId(Long userId, Long appointmentId);

    Optional<Review> findByUserIdAndAppointmentId(Long userId, Long appointmentId);
}
