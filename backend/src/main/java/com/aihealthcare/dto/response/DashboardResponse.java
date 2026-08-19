package com.aihealthcare.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardResponse {
    private String userName;
    private long totalPredictions;
    private long totalAppointments;
    private long totalPrescriptions;
    private long recommendedDoctorCount;
    private long unreadNotifications;
    private PredictionResponse latestPrediction;
    private List<DoctorResponse> recommendedDoctors;
    private List<AppointmentResponse> upcomingAppointments;
    private List<PrescriptionResponse> upcomingFollowUps;
    private Map<String, Long> diseaseFrequency;
    private List<MonthlyStat> monthlyPredictions;
    private List<DoctorResponse> availableDoctors;
    private List<NearbyPlaceResponse> nearbyPlaces;
    private List<PrescriptionResponse> recentPrescriptions;
    private List<DoctorResponse> favoriteDoctors;
}
