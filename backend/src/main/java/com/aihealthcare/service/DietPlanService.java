package com.aihealthcare.service;

import com.aihealthcare.dto.response.DietPlanResponse;
import com.aihealthcare.model.entity.DietPlan;
import com.aihealthcare.repository.DietPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DietPlanService {

    private final DietPlanRepository dietPlanRepository;

    public DietPlanResponse getByDisease(String diseaseName) {
        return dietPlanRepository.findByDiseaseNameIgnoreCase(diseaseName)
                .map(this::toResponse)
                .orElse(getDefaultPlan(diseaseName));
    }

    private DietPlanResponse getDefaultPlan(String diseaseName) {
        return DietPlanResponse.builder()
                .diseaseName(diseaseName)
                .dos("Stay hydrated|Get adequate rest|Eat balanced meals|Follow doctor advice")
                .donts("Avoid self-medication|Avoid strenuous activity|Avoid unhealthy food")
                .dietRecommendations("Eat fresh fruits and vegetables|Include protein-rich foods|Drink plenty of water")
                .generalAdvice("Consult a healthcare professional for personalized diet advice.")
                .build();
    }

    private DietPlanResponse toResponse(DietPlan plan) {
        return DietPlanResponse.builder()
                .diseaseName(plan.getDiseaseName())
                .dos(plan.getDos())
                .donts(plan.getDonts())
                .dietRecommendations(plan.getDietRecommendations())
                .generalAdvice(plan.getGeneralAdvice())
                .build();
    }
}
