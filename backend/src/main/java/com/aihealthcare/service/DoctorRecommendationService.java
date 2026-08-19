package com.aihealthcare.service;

import com.aihealthcare.dto.response.DoctorResponse;
import com.aihealthcare.model.entity.Doctor;
import com.aihealthcare.model.enums.DoctorApprovalStatus;
import com.aihealthcare.repository.DoctorRepository;
import com.aihealthcare.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorRecommendationService {

    private final DoctorRepository doctorRepository;
    private final ReviewRepository reviewRepository;

    private static final Map<String, List<String>> DISEASE_SPECIALIZATION_MAP = Map.ofEntries(
            Map.entry("Influenza", List.of("General Physician", "Infectious Disease Specialist")),
            Map.entry("Common Cold", List.of("General Physician", "ENT Specialist")),
            Map.entry("Pneumonia", List.of("Pulmonologist", "General Physician")),
            Map.entry("Asthma", List.of("Pulmonologist")),
            Map.entry("Bronchitis", List.of("Pulmonologist", "General Physician")),
            Map.entry("Gastroenteritis", List.of("Gastroenterologist", "General Physician")),
            Map.entry("Migraine", List.of("Neurologist", "General Physician")),
            Map.entry("Hypertension", List.of("Cardiologist", "General Physician")),
            Map.entry("Heart Disease", List.of("Cardiologist")),
            Map.entry("Diabetes", List.of("Endocrinologist", "General Physician")),
            Map.entry("Eczema", List.of("Dermatologist")),
            Map.entry("Allergic Reaction", List.of("Dermatologist", "General Physician")),
            Map.entry("Urinary Tract Infection", List.of("Nephrologist", "General Physician")),
            Map.entry("Anxiety Disorder", List.of("Psychiatrist")),
            Map.entry("Depression", List.of("Psychiatrist")),
            Map.entry("Conjunctivitis", List.of("Ophthalmologist")),
            Map.entry("Sinusitis", List.of("ENT Specialist")),
            Map.entry("Arthritis", List.of("Orthopedic", "General Physician")),
            Map.entry("COVID-19", List.of("Infectious Disease Specialist", "Pulmonologist")),
            Map.entry("Food Poisoning", List.of("Gastroenterologist", "General Physician"))
    );

    public String getRecommendedSpecialization(String disease) {
        List<String> specs = DISEASE_SPECIALIZATION_MAP.getOrDefault(disease, List.of("General Physician"));
        return specs.get(0);
    }

    public List<DoctorResponse> recommendDoctors(String disease, int limit) {
        List<String> specializations = DISEASE_SPECIALIZATION_MAP.getOrDefault(
                disease, List.of("General Physician"));

        List<Doctor> allDoctors = new ArrayList<>();
        for (String spec : specializations) {
            allDoctors.addAll(doctorRepository.findBySpecializationNameContainingIgnoreCase(spec));
        }

        if (allDoctors.isEmpty()) {
            allDoctors = doctorRepository.findAll();
        }

        return allDoctors.stream()
                .distinct()
                .filter(d -> d.getApprovalStatus() == DoctorApprovalStatus.APPROVED)
                .map(this::toDoctorResponseWithScore)
                .sorted(Comparator.comparing(DoctorResponse::getRecommendationScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    private DoctorResponse toDoctorResponseWithScore(Doctor doctor) {
        double ratingScore = doctor.getRating().doubleValue();
        double experienceScore = Math.min(doctor.getExperience() / 20.0, 1.0);
        double availabilityScore = (doctor.getAvailableDays() != null && !doctor.getAvailableDays().isEmpty()) ? 1.0 : 0.5;

        double recommendationScore = (ratingScore * 0.5) + (experienceScore * 0.3) + (availabilityScore * 0.2);

        return DoctorResponse.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .email(doctor.getEmail())
                .phone(doctor.getPhone())
                .specialization(doctor.getSpecialization().getName())
                .qualification(doctor.getQualification())
                .experience(doctor.getExperience())
                .rating(doctor.getRating())
                .consultationFee(doctor.getConsultationFee())
                .hospital(doctor.getHospital())
                .city(doctor.getCity())
                .availableDays(doctor.getAvailableDays())
                .availableTime(doctor.getAvailableTime())
                .bio(doctor.getBio())
                .licenseNumber(doctor.getLicenseNumber())
                .approvalStatus(doctor.getApprovalStatus() != null ? doctor.getApprovalStatus().name() : "APPROVED")
                .reviewCount(reviewRepository.countByDoctorId(doctor.getId()))
                .recommendationScore(Math.round(recommendationScore * 100.0) / 100.0)
                .build();
    }

    public DoctorResponse toDoctorResponse(Doctor doctor) {
        return toDoctorResponseWithScore(doctor);
    }
}
