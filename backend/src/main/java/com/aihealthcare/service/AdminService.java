package com.aihealthcare.service;

import com.aihealthcare.dto.response.AdminStatsResponse;
import com.aihealthcare.dto.response.AppointmentResponse;
import com.aihealthcare.dto.response.MonthlyStat;
import com.aihealthcare.dto.response.PredictionResponse;
import com.aihealthcare.exception.AppException;
import com.aihealthcare.model.entity.Doctor;
import com.aihealthcare.model.entity.User;
import com.aihealthcare.model.enums.AppointmentStatus;
import com.aihealthcare.model.enums.DoctorApprovalStatus;
import com.aihealthcare.model.enums.Role;
import com.aihealthcare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PredictionRepository predictionRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentService appointmentService;
    private final PredictionService predictionService;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public AdminStatsResponse getStats() {
        List<Map<String, Object>> predictionsByDisease = predictionRepository.countByDisease().stream()
                .map(row -> Map.<String, Object>of("disease", row[0], "count", row[1]))
                .toList();

        List<MonthlyStat> monthlyPredictions = predictionRepository.countByMonth().stream()
                .map(row -> new MonthlyStat(((Number) row[0]).intValue(), ((Number) row[1]).longValue()))
                .toList();

        List<MonthlyStat> monthlyRegistrations = userRepository.countByMonth().stream()
                .map(row -> new MonthlyStat(((Number) row[0]).intValue(), ((Number) row[1]).longValue()))
                .toList();

        return AdminStatsResponse.builder()
                .totalUsers(userRepository.countByRole(Role.USER))
                .totalDoctors(doctorRepository.count())
                .totalPredictions(predictionRepository.count())
                .totalAppointments(appointmentRepository.count())
                .pendingAppointments(appointmentRepository.countByStatus(AppointmentStatus.PENDING))
                .pendingDoctors(doctorRepository.countByApprovalStatus(DoctorApprovalStatus.PENDING))
                .predictionsByDisease(predictionsByDisease)
                .monthlyPredictions(monthlyPredictions)
                .monthlyRegistrations(monthlyRegistrations)
                .build();
    }

    public void setUserStatus(Long userId, boolean enabled, Long adminId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        user.setEnabled(enabled);
        userRepository.save(user);
        auditLogService.log(adminId, enabled ? "USER_UNBLOCKED" : "USER_BLOCKED", "USER", userId,
                (enabled ? "Unblocked" : "Blocked") + " user: " + user.getEmail());
    }

    @Transactional
    public void approveDoctor(Long doctorId, Long adminId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new AppException("Doctor not found", HttpStatus.NOT_FOUND));
        doctor.setApprovalStatus(DoctorApprovalStatus.APPROVED);
        doctorRepository.save(doctor);

        if (doctor.getUserId() != null) {
            notificationService.notify(doctor.getUserId(), "Profile Approved",
                    "Your doctor profile has been approved. You are now visible to patients.", "APPROVAL");
        }
        auditLogService.log(adminId, "DOCTOR_APPROVED", "DOCTOR", doctorId,
                "Approved doctor: " + doctor.getName());
    }

    @Transactional
    public void rejectDoctor(Long doctorId, Long adminId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new AppException("Doctor not found", HttpStatus.NOT_FOUND));
        doctor.setApprovalStatus(DoctorApprovalStatus.REJECTED);
        doctorRepository.save(doctor);

        if (doctor.getUserId() != null) {
            notificationService.notify(doctor.getUserId(), "Profile Rejected",
                    "Your doctor registration was not approved. Contact admin for details.", "APPROVAL");
        }
        auditLogService.log(adminId, "DOCTOR_REJECTED", "DOCTOR", doctorId,
                "Rejected doctor: " + doctor.getName());
    }

    public List<PredictionResponse> getAllPredictions() {
        return predictionRepository.findAll().stream()
                .map(p -> predictionService.getPrediction(p.getId(), p.getUserId()))
                .toList();
    }

    public List<AppointmentResponse> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }
}
