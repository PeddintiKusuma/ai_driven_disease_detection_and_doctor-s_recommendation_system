package com.aihealthcare.service;

import com.aihealthcare.dto.response.DocumentResponse;
import com.aihealthcare.dto.response.MedicalHistoryEntry;
import com.aihealthcare.model.entity.Appointment;
import com.aihealthcare.model.entity.Prescription;
import com.aihealthcare.model.entity.Prediction;
import com.aihealthcare.repository.AppointmentRepository;
import com.aihealthcare.repository.DoctorRepository;
import com.aihealthcare.repository.PrescriptionRepository;
import com.aihealthcare.repository.PredictionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final PredictionRepository predictionRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final DoctorRepository doctorRepository;

    public List<DocumentResponse> getUserDocuments(Long userId) {
        List<DocumentResponse> docs = new ArrayList<>();

        predictionRepository.findByUserIdOrderByCreatedAtDesc(userId).forEach(p ->
                docs.add(DocumentResponse.builder()
                        .id(p.getId())
                        .type("PREDICTION")
                        .title("Prediction Report — " + p.getPredictedDisease())
                        .description("AI prediction report for " + p.getPredictedDisease())
                        .createdAt(p.getCreatedAt())
                        .downloadUrl("/api/predictions/" + p.getId() + "/report")
                        .build()));

        appointmentRepository.findByUserIdOrderByAppointmentDateDesc(userId).forEach(a ->
                docs.add(DocumentResponse.builder()
                        .id(a.getId())
                        .type("APPOINTMENT")
                        .title("Appointment Letter — " + a.getDoctor().getName())
                        .description("Appointment on " + a.getAppointmentDate())
                        .createdAt(a.getCreatedAt())
                        .downloadUrl("/api/appointments/" + a.getId() + "/letter")
                        .build()));

        prescriptionRepository.findByPatientIdOrderByCreatedAtDesc(userId).forEach(rx -> {
            String doctorName = doctorRepository.findById(rx.getDoctorId())
                    .map(d -> d.getName()).orElse("Unknown");
            docs.add(DocumentResponse.builder()
                    .id(rx.getId())
                    .type("PRESCRIPTION")
                    .title("Prescription — " + rx.getDiagnosis())
                    .description("Prescription from " + doctorName)
                    .createdAt(rx.getCreatedAt())
                    .downloadUrl("/api/prescriptions/" + rx.getId() + "/download")
                    .build());
        });

        docs.sort(Comparator.comparing(DocumentResponse::getCreatedAt).reversed());
        return docs;
    }

    public List<MedicalHistoryEntry> getMedicalHistory(Long userId) {
        List<MedicalHistoryEntry> history = new ArrayList<>();

        predictionRepository.findByUserIdOrderByCreatedAtDesc(userId).forEach(p ->
                history.add(MedicalHistoryEntry.builder()
                        .date(p.getCreatedAt().toLocalDate())
                        .type("PREDICTION")
                        .description(p.getPredictedDisease() + " (" + p.getRiskLevel() + " risk)")
                        .referenceId(p.getId())
                        .build()));

        appointmentRepository.findByUserIdOrderByAppointmentDateDesc(userId).forEach(a ->
                history.add(MedicalHistoryEntry.builder()
                        .date(a.getAppointmentDate())
                        .type("APPOINTMENT")
                        .description(a.getReason())
                        .doctorName(a.getDoctor().getName())
                        .referenceId(a.getId())
                        .build()));

        prescriptionRepository.findByPatientIdOrderByCreatedAtDesc(userId).forEach(rx -> {
            String doctorName = doctorRepository.findById(rx.getDoctorId())
                    .map(d -> d.getName()).orElse("Unknown");
            history.add(MedicalHistoryEntry.builder()
                    .date(rx.getCreatedAt().toLocalDate())
                    .type("PRESCRIPTION")
                    .description(rx.getDiagnosis())
                    .doctorName(doctorName)
                    .referenceId(rx.getId())
                    .build());
        });

        history.sort(Comparator.comparing(MedicalHistoryEntry::getDate).reversed());
        return history;
    }
}
