package com.aihealthcare.service;

import com.aihealthcare.dto.request.DoctorProfileUpdateRequest;
import com.aihealthcare.dto.request.DoctorScheduleUpdateRequest;
import com.aihealthcare.dto.response.DoctorResponse;
import com.aihealthcare.exception.AppException;
import com.aihealthcare.model.entity.Doctor;
import com.aihealthcare.model.entity.Review;
import com.aihealthcare.model.enums.DoctorApprovalStatus;
import com.aihealthcare.repository.DoctorRepository;
import com.aihealthcare.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final ReviewRepository reviewRepository;
    private final DoctorRecommendationService recommendationService;

    public List<DoctorResponse> getAllDoctors() {
        return doctorRepository.findByApprovalStatus(DoctorApprovalStatus.APPROVED).stream()
                .map(recommendationService::toDoctorResponse)
                .collect(Collectors.toList());
    }

    public List<DoctorResponse> getAllDoctorsAdmin() {
        return doctorRepository.findAll().stream()
                .map(recommendationService::toDoctorResponse)
                .collect(Collectors.toList());
    }

    public DoctorResponse getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new AppException("Doctor not found", HttpStatus.NOT_FOUND));
        return recommendationService.toDoctorResponse(doctor);
    }

    public Map<String, Object> getDoctorProfile(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new AppException("Doctor not found", HttpStatus.NOT_FOUND));

        List<Review> reviews = reviewRepository.findByDoctorIdOrderByCreatedAtDesc(id);

        Map<String, Object> profile = new HashMap<>();
        profile.put("doctor", recommendationService.toDoctorResponse(doctor));
        profile.put("reviews", reviews.stream().map(r -> Map.of(
                "id", r.getId(),
                "rating", r.getRating(),
                "comment", r.getComment() != null ? r.getComment() : "",
                "createdAt", r.getCreatedAt().toString()
        )).collect(Collectors.toList()));
        profile.put("reviewCount", reviews.size());
        return profile;
    }

    public List<DoctorResponse> searchBySpecialization(String specialization) {
        return doctorRepository.findBySpecializationNameContainingIgnoreCase(specialization).stream()
                .filter(d -> d.getApprovalStatus() == DoctorApprovalStatus.APPROVED)
                .map(recommendationService::toDoctorResponse)
                .collect(Collectors.toList());
    }

    public List<DoctorResponse> searchDoctors(String query, String specialization, String city,
                                               BigDecimal minFee, BigDecimal maxFee, Double minRating) {
        return doctorRepository.findByApprovalStatus(DoctorApprovalStatus.APPROVED).stream()
                .filter(d -> query == null || query.isBlank() ||
                        d.getName().toLowerCase().contains(query.toLowerCase()) ||
                        d.getHospital().toLowerCase().contains(query.toLowerCase()))
                .filter(d -> specialization == null || specialization.isBlank() ||
                        d.getSpecialization().getName().equalsIgnoreCase(specialization))
                .filter(d -> city == null || city.isBlank() ||
                        (d.getCity() != null && d.getCity().equalsIgnoreCase(city)))
                .filter(d -> minFee == null || d.getConsultationFee().compareTo(minFee) >= 0)
                .filter(d -> maxFee == null || d.getConsultationFee().compareTo(maxFee) <= 0)
                .filter(d -> minRating == null || d.getRating().doubleValue() >= minRating)
                .map(recommendationService::toDoctorResponse)
                .collect(Collectors.toList());
    }

    public Doctor getDoctorByUserId(Long userId) {
        return doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException("Doctor profile not found", HttpStatus.NOT_FOUND));
    }

    public DoctorResponse getProfileByUserId(Long userId) {
        return recommendationService.toDoctorResponse(getDoctorByUserId(userId));
    }

    @Transactional
    public DoctorResponse updateProfile(Long userId, DoctorProfileUpdateRequest request) {
        Doctor doctor = getDoctorByUserId(userId);
        if (request.getPhone() != null) doctor.setPhone(request.getPhone());
        if (request.getQualification() != null) doctor.setQualification(request.getQualification());
        if (request.getHospital() != null) doctor.setHospital(request.getHospital());
        if (request.getCity() != null) doctor.setCity(request.getCity());
        if (request.getBio() != null) doctor.setBio(request.getBio());
        if (request.getConsultationFee() != null) doctor.setConsultationFee(request.getConsultationFee());
        return recommendationService.toDoctorResponse(doctorRepository.save(doctor));
    }

    @Transactional
    public DoctorResponse updateSchedule(Long userId, DoctorScheduleUpdateRequest request) {
        Doctor doctor = getDoctorByUserId(userId);
        doctor.setAvailableDays(request.getAvailableDays());
        doctor.setAvailableTime(request.getAvailableTime());
        return recommendationService.toDoctorResponse(doctorRepository.save(doctor));
    }
}
