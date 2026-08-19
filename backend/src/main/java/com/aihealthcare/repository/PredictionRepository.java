package com.aihealthcare.repository;

import com.aihealthcare.model.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    List<Prediction> findByUserIdOrderByCreatedAtDesc(Long userId);
    long countByUserId(Long userId);

    @Query("SELECT p.predictedDisease, COUNT(p) FROM Prediction p GROUP BY p.predictedDisease ORDER BY COUNT(p) DESC")
    List<Object[]> countByDisease();

    @Query(value = "SELECT MONTH(created_at) as month, COUNT(*) as count FROM predictions GROUP BY MONTH(created_at)", nativeQuery = true)
    List<Object[]> countByMonth();
}
