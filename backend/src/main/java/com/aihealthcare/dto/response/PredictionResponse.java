package com.aihealthcare.dto.response;

import com.aihealthcare.model.enums.RiskLevel;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PredictionResponse {
    private Long id;
    private String predictedDisease;
    private BigDecimal confidence;
    private RiskLevel riskLevel;
    private Integer age;
    private String gender;
    private String recommendedSpecialization;
    private List<PossibleDiseaseDto> possibleDiseases;
    private List<String> symptoms;
    private List<DoctorResponse> recommendedDoctors;
    private List<String> warnings;
    private DietPlanResponse dietPlan;
    private LocalDateTime createdAt;
}
