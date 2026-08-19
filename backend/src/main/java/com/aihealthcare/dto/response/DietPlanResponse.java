package com.aihealthcare.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DietPlanResponse {
    private String diseaseName;
    private String dos;
    private String donts;
    private String dietRecommendations;
    private String generalAdvice;
}
