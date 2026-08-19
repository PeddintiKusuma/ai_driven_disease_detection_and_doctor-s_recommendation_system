package com.aihealthcare.service;

import com.aihealthcare.dto.response.*;
import com.aihealthcare.model.entity.Prediction;
import com.aihealthcare.model.entity.User;
import com.aihealthcare.model.enums.AppointmentStatus;
import com.aihealthcare.model.enums.DoctorApprovalStatus;
import com.aihealthcare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PredictionRepository predictionRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorRecommendationService doctorRecommendationService;
    private final AppointmentService appointmentService;
    private final PredictionService predictionService;
    private final NearbyPlaceService nearbyPlaceService;
    private final PrescriptionService prescriptionService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final FavoriteDoctorService favoriteDoctorService;

    public DashboardResponse getUserDashboard(Long userId) {
        List<Prediction> predictions = predictionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        User user = userRepository.findById(userId).orElse(null);
        String city = user != null && user.getCity() != null ? user.getCity() : "Hyderabad";

        PredictionResponse latest = predictions.isEmpty() ? null :
                predictionService.getPrediction(predictions.get(0).getId(), userId);

        List<DoctorResponse> recommendedDoctors = latest != null ?
                doctorRecommendationService.recommendDoctors(latest.getPredictedDisease(), 8) :
                doctorRepository.findByApprovalStatus(DoctorApprovalStatus.APPROVED).stream()
                        .map(doctorRecommendationService::toDoctorResponse)
                        .limit(8).collect(Collectors.toList());

        List<AppointmentResponse> allAppointments = appointmentService.getUserAppointments(userId);
        List<AppointmentResponse> upcoming = allAppointments.stream()
                .filter(a -> !a.getAppointmentDate().isBefore(LocalDate.now())
                        && a.getStatus() != AppointmentStatus.CANCELLED
                        && a.getStatus() != AppointmentStatus.COMPLETED)
                .limit(5)
                .collect(Collectors.toList());

        List<PrescriptionResponse> prescriptions = prescriptionService.getPatientPrescriptions(userId);
        List<PrescriptionResponse> followUps = prescriptions.stream()
                .filter(p -> p.getFollowUpDate() != null && !p.getFollowUpDate().isBlank())
                .filter(p -> LocalDate.parse(p.getFollowUpDate()).isAfter(LocalDate.now().minusDays(1)))
                .limit(3)
                .collect(Collectors.toList());

        Map<String, Long> diseaseFrequency = predictions.stream()
                .collect(Collectors.groupingBy(Prediction::getPredictedDisease, Collectors.counting()));

        return DashboardResponse.builder()
                .userName(user != null ? user.getFirstName() : "Patient")
                .totalPredictions(predictions.size())
                .totalAppointments(allAppointments.size())
                .totalPrescriptions(prescriptions.size())
                .recommendedDoctorCount(recommendedDoctors.size())
                .latestPrediction(latest)
                .recommendedDoctors(recommendedDoctors)
                .availableDoctors(doctorRepository.findByApprovalStatus(DoctorApprovalStatus.APPROVED).stream()
                        .map(doctorRecommendationService::toDoctorResponse)
                        .sorted(Comparator.comparing(DoctorResponse::getRating).reversed())
                        .limit(8).collect(Collectors.toList()))
                .upcomingAppointments(upcoming)
                .upcomingFollowUps(followUps)
                .diseaseFrequency(diseaseFrequency)
                .nearbyPlaces(nearbyPlaceService.getNearby(city, null).stream().limit(8).collect(Collectors.toList()))
                .recentPrescriptions(prescriptions.stream().limit(5).collect(Collectors.toList()))
                .favoriteDoctors(favoriteDoctorService.getFavorites(userId))
                .unreadNotifications(notificationService.getUnreadCount(userId))
                .build();
    }
}
