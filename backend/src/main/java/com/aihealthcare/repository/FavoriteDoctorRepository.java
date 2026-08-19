package com.aihealthcare.repository;

import com.aihealthcare.model.entity.FavoriteDoctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteDoctorRepository extends JpaRepository<FavoriteDoctor, Long> {
    List<FavoriteDoctor> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<FavoriteDoctor> findByUserIdAndDoctorId(Long userId, Long doctorId);
    boolean existsByUserIdAndDoctorId(Long userId, Long doctorId);
}
