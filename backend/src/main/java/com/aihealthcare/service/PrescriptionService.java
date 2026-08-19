package com.aihealthcare.service;

import com.aihealthcare.dto.request.PrescriptionRequest;
import com.aihealthcare.dto.response.PrescriptionResponse;
import com.aihealthcare.exception.AppException;
import com.aihealthcare.model.entity.Appointment;
import com.aihealthcare.model.entity.Doctor;
import com.aihealthcare.model.entity.Prescription;
import com.aihealthcare.model.entity.User;
import com.aihealthcare.repository.AppointmentRepository;
import com.aihealthcare.repository.DoctorRepository;
import com.aihealthcare.repository.PrescriptionRepository;
import com.aihealthcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @Transactional
    public PrescriptionResponse createPrescription(Long doctorUserId, PrescriptionRequest request) {
        Doctor doctor = doctorRepository.findByUserId(doctorUserId)
                .orElseThrow(() -> new AppException("Doctor profile not found", HttpStatus.NOT_FOUND));

        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new AppException("Appointment not found", HttpStatus.NOT_FOUND));

        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new AppException("This appointment is not assigned to you", HttpStatus.FORBIDDEN);
        }

        prescriptionRepository.findByAppointmentId(request.getAppointmentId()).ifPresent(p -> {
            throw new AppException("Prescription already exists for this appointment", HttpStatus.CONFLICT);
        });

        Prescription prescription = Prescription.builder()
                .appointmentId(appointment.getId())
                .doctorId(doctor.getId())
                .patientId(appointment.getUserId())
                .diagnosis(request.getDiagnosis())
                .medicines(request.getMedicines())
                .instructions(request.getInstructions())
                .followUpDate(request.getFollowUpDate())
                .build();

        Prescription saved = prescriptionRepository.save(prescription);

        notificationService.notify(appointment.getUserId(), "Prescription Available",
                doctor.getName() + " has uploaded your prescription.", "PRESCRIPTION");
        auditLogService.log(doctorUserId, "PRESCRIPTION_CREATED", "PRESCRIPTION", saved.getId(),
                "Prescription for patient " + appointment.getUserId());

        return toResponse(saved);
    }

    public List<PrescriptionResponse> getPatientPrescriptions(Long patientId) {
        return prescriptionRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<PrescriptionResponse> getDoctorPrescriptions(Long doctorUserId) {
        Doctor doctor = doctorRepository.findByUserId(doctorUserId)
                .orElseThrow(() -> new AppException("Doctor profile not found", HttpStatus.NOT_FOUND));
        return prescriptionRepository.findByDoctorIdOrderByCreatedAtDesc(doctor.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public PrescriptionResponse getById(Long id, Long userId, String role) {
        Prescription p = prescriptionRepository.findById(id)
                .orElseThrow(() -> new AppException("Prescription not found", HttpStatus.NOT_FOUND));

        if ("USER".equals(role) && !p.getPatientId().equals(userId)) {
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        }
        if ("DOCTOR".equals(role)) {
            Doctor doctor = doctorRepository.findByUserId(userId)
                    .orElseThrow(() -> new AppException("Doctor not found", HttpStatus.NOT_FOUND));
            if (!p.getDoctorId().equals(doctor.getId())) {
                throw new AppException("Access denied", HttpStatus.FORBIDDEN);
            }
        }
        return toResponse(p);
    }

    private PrescriptionResponse toResponse(Prescription p) {
        Doctor doctor = doctorRepository.findById(p.getDoctorId()).orElse(null);
        User patient = userRepository.findById(p.getPatientId()).orElse(null);

        return PrescriptionResponse.builder()
                .id(p.getId())
                .appointmentId(p.getAppointmentId())
                .doctorId(p.getDoctorId())
                .doctorName(doctor != null ? doctor.getName() : "Unknown")
                .patientId(p.getPatientId())
                .patientName(patient != null ? patient.getFirstName() + " " + patient.getLastName() : "Unknown")
                .diagnosis(p.getDiagnosis())
                .medicines(p.getMedicines())
                .instructions(p.getInstructions())
                .followUpDate(p.getFollowUpDate())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
