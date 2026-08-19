package com.aihealthcare.service;

import com.aihealthcare.dto.request.AppointmentRequest;
import com.aihealthcare.dto.response.AppointmentResponse;
import com.aihealthcare.dto.response.DoctorResponse;
import com.aihealthcare.exception.AppException;
import com.aihealthcare.model.entity.Appointment;
import com.aihealthcare.model.entity.Doctor;
import com.aihealthcare.model.entity.User;
import com.aihealthcare.model.enums.AppointmentStatus;
import com.aihealthcare.model.enums.DoctorApprovalStatus;
import com.aihealthcare.repository.AppointmentRepository;
import com.aihealthcare.repository.DoctorRepository;
import com.aihealthcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final DoctorRecommendationService recommendationService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;
    private final AppointmentSlotService slotService;

    @Transactional
    public AppointmentResponse createAppointment(Long userId, AppointmentRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new AppException("Doctor not found", HttpStatus.NOT_FOUND));

        if (doctor.getApprovalStatus() != DoctorApprovalStatus.APPROVED) {
            throw new AppException("This doctor is not available for booking", HttpStatus.BAD_REQUEST);
        }

        if (request.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new AppException("Cannot book appointments in the past", HttpStatus.BAD_REQUEST);
        }

        slotService.validateBooking(request.getDoctorId(), request.getAppointmentDate(),
                request.getAppointmentTime(), null);

        Appointment appointment = Appointment.builder()
                .userId(userId)
                .doctor(doctor)
                .predictionId(request.getPredictionId())
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .reason(request.getReason())
                .notes(request.getNotes())
                .status(AppointmentStatus.PENDING)
                .build();

        Appointment saved = appointmentRepository.save(appointment);

        User patient = userRepository.findById(userId).orElse(null);
        String patientName = patient != null ? patient.getFirstName() + " " + patient.getLastName() : "A patient";

        if (doctor.getUserId() != null) {
            notificationService.notify(doctor.getUserId(), "New Appointment Request",
                    patientName + " requested an appointment on " + request.getAppointmentDate(), "APPOINTMENT");
        }
        notificationService.notify(userId, "Appointment Booked",
                "Your appointment with " + doctor.getName() + " is pending confirmation.", "APPOINTMENT");

        auditLogService.log(userId, "APPOINTMENT_BOOKED", "APPOINTMENT", saved.getId(),
                "Booked with " + doctor.getName());

        return toResponse(saved);
    }

    @Transactional
    public AppointmentResponse rescheduleAppointment(Long appointmentId, Long userId,
                                                     LocalDate newDate, LocalTime newTime) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppException("Appointment not found", HttpStatus.NOT_FOUND));

        if (!appointment.getUserId().equals(userId)) {
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        }
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new AppException("Completed appointments cannot be rescheduled", HttpStatus.BAD_REQUEST);
        }
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new AppException("Cancelled appointments cannot be rescheduled", HttpStatus.BAD_REQUEST);
        }

        slotService.validateBooking(
                appointment.getDoctor().getId(), newDate, newTime, appointmentId);

        appointment.setAppointmentDate(newDate);
        appointment.setAppointmentTime(newTime);
        appointment.setStatus(AppointmentStatus.PENDING);
        Appointment saved = appointmentRepository.save(appointment);

        User patient = userRepository.findById(userId).orElse(null);
        String patientName = patient != null ? patient.getFirstName() + " " + patient.getLastName() : "A patient";
        Doctor doctor = appointment.getDoctor();

        if (doctor.getUserId() != null) {
            notificationService.notify(doctor.getUserId(), "Appointment Rescheduled",
                    patientName + " rescheduled to " + newDate + " at " + newTime, "APPOINTMENT");
        }
        notificationService.notify(userId, "Appointment Rescheduled",
                "Your appointment with " + doctor.getName() + " has been moved to " + newDate + " at " + newTime,
                "APPOINTMENT");
        auditLogService.log(userId, "APPOINTMENT_RESCHEDULED", "APPOINTMENT", appointmentId,
                "Rescheduled to " + newDate + " " + newTime);

        return toResponse(saved);
    }

    public List<AppointmentResponse> getUserAppointments(Long userId) {
        return appointmentRepository.findByUserIdOrderByAppointmentDateDesc(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponse> getDoctorAppointments(Long doctorId) {
        return appointmentRepository.findByDoctorIdOrderByAppointmentDateDesc(doctorId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponse updateStatus(Long appointmentId, AppointmentStatus status, Long requesterId, String role) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppException("Appointment not found", HttpStatus.NOT_FOUND));

        if ("DOCTOR".equals(role)) {
            Doctor doctor = doctorRepository.findByUserId(requesterId)
                    .orElseThrow(() -> new AppException("Doctor profile not found", HttpStatus.NOT_FOUND));
            if (!appointment.getDoctor().getId().equals(doctor.getId())) {
                throw new AppException("Access denied", HttpStatus.FORBIDDEN);
            }
        } else if (!"ADMIN".equals(role) && !appointment.getUserId().equals(requesterId)) {
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        }

        appointment.setStatus(status);
        Appointment saved = appointmentRepository.save(appointment);

        String doctorName = appointment.getDoctor().getName();
        switch (status) {
            case CONFIRMED -> {
                notificationService.notify(appointment.getUserId(), "Appointment Confirmed",
                        doctorName + " confirmed your appointment on " + appointment.getAppointmentDate(), "APPOINTMENT");
                auditLogService.log(requesterId, "APPOINTMENT_CONFIRMED", "APPOINTMENT", appointmentId,
                        doctorName + " confirmed appointment");
            }
            case CANCELLED -> {
                notificationService.notify(appointment.getUserId(), "Appointment Cancelled",
                        "Your appointment with " + doctorName + " has been cancelled.", "APPOINTMENT");
                if (appointment.getDoctor().getUserId() != null) {
                    notificationService.notify(appointment.getDoctor().getUserId(), "Appointment Cancelled",
                            "An appointment on " + appointment.getAppointmentDate() + " was cancelled.", "APPOINTMENT");
                }
                auditLogService.log(requesterId, "APPOINTMENT_CANCELLED", "APPOINTMENT", appointmentId,
                        "Appointment cancelled");
            }
            case COMPLETED -> {
                notificationService.notify(appointment.getUserId(), "Appointment Completed",
                        "Your consultation with " + doctorName + " is complete. You can now leave a review.", "APPOINTMENT");
                auditLogService.log(requesterId, "APPOINTMENT_COMPLETED", "APPOINTMENT", appointmentId,
                        "Appointment marked complete");
            }
            default -> {}
        }

        return toResponse(saved);
    }

    @Transactional
    public AppointmentResponse revertCompletion(Long appointmentId, Long doctorUserId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppException("Appointment not found", HttpStatus.NOT_FOUND));

        Doctor doctor = doctorRepository.findByUserId(doctorUserId)
                .orElseThrow(() -> new AppException("Doctor profile not found", HttpStatus.NOT_FOUND));
        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        }
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new AppException("Only completed appointments can be reverted", HttpStatus.BAD_REQUEST);
        }

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        Appointment saved = appointmentRepository.save(appointment);

        notificationService.notify(appointment.getUserId(), "Appointment Status Updated",
                doctor.getName() + " reopened your appointment on " + appointment.getAppointmentDate(), "APPOINTMENT");
        auditLogService.log(doctorUserId, "APPOINTMENT_REOPENED", "APPOINTMENT", appointmentId,
                "Completed appointment reverted to confirmed");

        return toResponse(saved);
    }

    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public AppointmentResponse getById(Long id) {
        return appointmentRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new AppException("Appointment not found", HttpStatus.NOT_FOUND));
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        User user = userRepository.findById(appointment.getUserId()).orElse(null);
        DoctorResponse doctorResponse = recommendationService.toDoctorResponse(appointment.getDoctor());

        return AppointmentResponse.builder()
                .id(appointment.getId())
                .userId(appointment.getUserId())
                .userName(user != null ? user.getFirstName() + " " + user.getLastName() : "Unknown")
                .doctor(doctorResponse)
                .predictionId(appointment.getPredictionId())
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentTime(appointment.getAppointmentTime())
                .reason(appointment.getReason())
                .notes(appointment.getNotes())
                .status(appointment.getStatus())
                .createdAt(appointment.getCreatedAt())
                .build();
    }
}
