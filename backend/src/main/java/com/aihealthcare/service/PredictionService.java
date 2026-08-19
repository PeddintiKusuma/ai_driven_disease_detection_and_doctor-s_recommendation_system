package com.aihealthcare.service;

import com.aihealthcare.dto.request.PredictionRequest;
import com.aihealthcare.dto.response.DoctorResponse;
import com.aihealthcare.dto.response.DietPlanResponse;
import com.aihealthcare.dto.response.PossibleDiseaseDto;
import com.aihealthcare.dto.response.PredictionResponse;
import com.aihealthcare.exception.AppException;
import com.aihealthcare.model.entity.Prediction;
import com.aihealthcare.model.entity.PredictionSymptom;
import com.aihealthcare.model.entity.User;
import com.aihealthcare.model.enums.RiskLevel;
import com.aihealthcare.repository.PredictionRepository;
import com.aihealthcare.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PredictionService {

    private final PredictionRepository predictionRepository;
    private final UserRepository userRepository;
    private final MlServiceClient mlServiceClient;
    private final DoctorRecommendationService doctorRecommendationService;
    private final DietPlanService dietPlanService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public PredictionResponse createPrediction(Long userId, PredictionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        Integer age = request.getAge() != null ? request.getAge() : user.getAge();
        String gender = request.getGender() != null ? request.getGender().name() :
                (user.getGender() != null ? user.getGender().name() : "OTHER");

        Map<String, Object> mlResult = mlServiceClient.predict(age, gender, request.getSymptoms());

        if (!(Boolean) mlResult.get("success")) {
            throw new AppException("ML prediction failed", HttpStatus.BAD_GATEWAY);
        }

        String disease = (String) mlResult.get("disease");
        double confidence = (Double) mlResult.get("confidence");
        RiskLevel riskLevel = RiskLevel.valueOf((String) mlResult.get("riskLevel"));
        String specialization = doctorRecommendationService.getRecommendedSpecialization(disease);

        String possibleDiseasesJson;
        try {
            possibleDiseasesJson = objectMapper.writeValueAsString(mlResult.get("possibleDiseases"));
        } catch (JsonProcessingException e) {
            possibleDiseasesJson = "[]";
        }

        Prediction prediction = Prediction.builder()
                .userId(userId)
                .predictedDisease(disease)
                .confidence(BigDecimal.valueOf(confidence))
                .riskLevel(riskLevel)
                .age(age)
                .gender(gender)
                .recommendedSpecialization(specialization)
                .possibleDiseases(possibleDiseasesJson)
                .build();

        for (String symptom : request.getSymptoms()) {
            PredictionSymptom ps = PredictionSymptom.builder()
                    .prediction(prediction)
                    .symptomName(symptom)
                    .build();
            prediction.getSymptoms().add(ps);
        }

        prediction = predictionRepository.save(prediction);
        List<DoctorResponse> doctors = doctorRecommendationService.recommendDoctors(disease, 5);

        return buildPredictionResponse(prediction, doctors, mlResult);
    }

    public PredictionResponse getPrediction(Long id, Long userId) {
        Prediction prediction = predictionRepository.findById(id)
                .orElseThrow(() -> new AppException("Prediction not found", HttpStatus.NOT_FOUND));

        if (!prediction.getUserId().equals(userId)) {
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        }

        List<DoctorResponse> doctors = doctorRecommendationService.recommendDoctors(
                prediction.getPredictedDisease(), 5);
        return buildPredictionResponse(prediction, doctors, null);
    }

    public List<PredictionResponse> getUserPredictions(Long userId) {
        return predictionRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(p -> buildPredictionResponse(p, List.of(), null))
                .collect(Collectors.toList());
    }

    private PredictionResponse buildPredictionResponse(Prediction prediction,
                                                        List<DoctorResponse> doctors,
                                                        Map<String, Object> mlResult) {
        List<PossibleDiseaseDto> possibleDiseases = parsePossibleDiseases(prediction.getPossibleDiseases());
        List<String> symptoms = prediction.getSymptoms().stream()
                .map(PredictionSymptom::getSymptomName)
                .collect(Collectors.toList());

        List<String> warnings = new ArrayList<>();
        if (mlResult != null && mlResult.containsKey("warnings")) {
            JsonNode warningsNode = objectMapper.valueToTree(mlResult.get("warnings"));
            warningsNode.forEach(w -> warnings.add(w.asText()));
        }

        DietPlanResponse dietPlan = dietPlanService.getByDisease(prediction.getPredictedDisease());

        return PredictionResponse.builder()
                .id(prediction.getId())
                .predictedDisease(prediction.getPredictedDisease())
                .confidence(prediction.getConfidence())
                .riskLevel(prediction.getRiskLevel())
                .age(prediction.getAge())
                .gender(prediction.getGender())
                .recommendedSpecialization(prediction.getRecommendedSpecialization())
                .possibleDiseases(possibleDiseases)
                .symptoms(symptoms)
                .recommendedDoctors(doctors)
                .warnings(warnings)
                .dietPlan(dietPlan)
                .createdAt(prediction.getCreatedAt())
                .build();
    }

    private List<PossibleDiseaseDto> parsePossibleDiseases(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            List<PossibleDiseaseDto> list = new ArrayList<>();
            node.forEach(item -> list.add(PossibleDiseaseDto.builder()
                    .name(item.get("name").asText())
                    .confidence(BigDecimal.valueOf(item.get("confidence").asDouble()))
                    .build()));
            return list;
        } catch (Exception e) {
            return List.of();
        }
    }
}
