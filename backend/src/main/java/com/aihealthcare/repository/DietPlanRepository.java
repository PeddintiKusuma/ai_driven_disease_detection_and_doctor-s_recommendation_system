package com.aihealthcare.repository;

import com.aihealthcare.model.entity.DietPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DietPlanRepository extends JpaRepository<DietPlan, Long> {
    Optional<DietPlan> findByDiseaseNameIgnoreCase(String diseaseName);
}
