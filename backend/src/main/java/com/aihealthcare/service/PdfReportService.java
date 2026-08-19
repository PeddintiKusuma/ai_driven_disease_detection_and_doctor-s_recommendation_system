package com.aihealthcare.service;

import com.aihealthcare.exception.AppException;
import com.aihealthcare.model.entity.*;
import com.aihealthcare.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PdfReportService {

    private final PredictionRepository predictionRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final DoctorRepository doctorRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public byte[] generatePredictionReport(Long predictionId, Long userId) {
        Prediction prediction = predictionRepository.findById(predictionId)
                .orElseThrow(() -> new AppException("Prediction not found", HttpStatus.NOT_FOUND));
        if (!prediction.getUserId().equals(userId)) {
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            String docId = "PRED-" + prediction.getId();
            String generatedAt = prediction.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));

            Document document = PdfBrandingHelper.openDocumentWithPageBranding(baos, new PdfBrandingConfig(
                    "AI Prediction Report",
                    "Preliminary health analysis summary",
                    "Report No.",
                    docId,
                    docId
            ));

            PdfBrandingHelper.addSectionTitle(document, "Patient Information");
            Table patientTable = PdfBrandingHelper.createInfoTable();
            PdfBrandingHelper.addInfoRow(patientTable, "Patient Name", user.getFirstName() + " " + user.getLastName());
            PdfBrandingHelper.addInfoRow(patientTable, "Email", user.getEmail());
            PdfBrandingHelper.addInfoRow(patientTable, "Report Date", generatedAt);
            document.add(patientTable);

            PdfBrandingHelper.addSectionTitle(document, "Prediction Summary");
            Table resultTable = PdfBrandingHelper.createInfoTable();
            PdfBrandingHelper.addInfoRow(resultTable, "Predicted Condition", prediction.getPredictedDisease());
            PdfBrandingHelper.addInfoRow(resultTable, "Confidence Score",
                    String.format("%.1f%%", prediction.getConfidence().doubleValue() * 100));
            PdfBrandingHelper.addInfoRow(resultTable, "Risk Level", prediction.getRiskLevel().name());
            PdfBrandingHelper.addInfoRow(resultTable, "Recommended Specialist",
                    prediction.getRecommendedSpecialization() != null ? prediction.getRecommendedSpecialization() : "General Physician");
            document.add(resultTable);

            String symptoms = prediction.getSymptoms().stream()
                    .map(PredictionSymptom::getSymptomName)
                    .map(this::formatSymptom)
                    .collect(Collectors.joining(", "));
            PdfBrandingHelper.addContentBlock(document, "Reported Symptoms", symptoms.isBlank() ? "None recorded" : symptoms);

            String possibleDiseases = formatPossibleDiseases(prediction.getPossibleDiseases());
            if (!possibleDiseases.isBlank()) {
                PdfBrandingHelper.addContentBlock(document, "Other Possible Conditions", possibleDiseases);
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new AppException("Failed to generate PDF report", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public byte[] generateAppointmentLetter(Long appointmentId, Long userId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppException("Appointment not found", HttpStatus.NOT_FOUND));
        if (!appointment.getUserId().equals(userId)) {
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        }
        User patient = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        Doctor doctor = appointment.getDoctor();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            String docId = "APT-" + appointment.getId();

            Document document = PdfBrandingHelper.openDocumentWithPageBranding(baos, new PdfBrandingConfig(
                    "Appointment Letter",
                    "Official consultation confirmation",
                    "Appointment ID",
                    docId,
                    docId
            ));

            document.add(new Paragraph("Dear " + patient.getFirstName() + " " + patient.getLastName() + ",")
                    .setFontSize(10)
                    .setMarginBottom(8));
            document.add(new Paragraph(
                    "Your appointment has been scheduled through Healix. Please review the details below and carry this letter to your consultation.")
                    .setFontSize(10)
                    .setMarginBottom(4));

            PdfBrandingHelper.addSectionTitle(document, "Patient Details");
            Table patientTable = PdfBrandingHelper.createInfoTable();
            PdfBrandingHelper.addInfoRow(patientTable, "Patient Name", patient.getFirstName() + " " + patient.getLastName());
            PdfBrandingHelper.addInfoRow(patientTable, "Email", patient.getEmail());
            PdfBrandingHelper.addInfoRow(patientTable, "Phone", patient.getPhone());
            document.add(patientTable);

            PdfBrandingHelper.addSectionTitle(document, "Consultation Details");
            Table appointmentTable = PdfBrandingHelper.createInfoTable();
            PdfBrandingHelper.addInfoRow(appointmentTable, "Doctor", doctor.getName());
            PdfBrandingHelper.addInfoRow(appointmentTable, "Specialization", doctor.getSpecialization().getName());
            PdfBrandingHelper.addInfoRow(appointmentTable, "Hospital / Clinic", doctor.getHospital());
            PdfBrandingHelper.addInfoRow(appointmentTable, "Location", doctor.getCity());
            PdfBrandingHelper.addInfoRow(appointmentTable, "Date", appointment.getAppointmentDate().toString());
            PdfBrandingHelper.addInfoRow(appointmentTable, "Time", appointment.getAppointmentTime().toString());
            PdfBrandingHelper.addInfoRow(appointmentTable, "Consultation Fee", "₹" + doctor.getConsultationFee());
            PdfBrandingHelper.addInfoRow(appointmentTable, "Status", appointment.getStatus().name());
            PdfBrandingHelper.addInfoRow(appointmentTable, "Reason for Visit",
                    appointment.getReason() != null ? appointment.getReason() : "General consultation");
            document.add(appointmentTable);

            PdfBrandingHelper.addNoteList(document,
                    "Please arrive at least 10 minutes before your scheduled time.",
                    "Bring this appointment letter and a valid government-issued photo ID.",
                    "If you need to reschedule, contact the clinic or update your booking through Healix.",
                    "Late arrival may result in rescheduling depending on doctor availability.");

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new AppException("Failed to generate appointment letter", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public byte[] generatePrescriptionPdf(Long prescriptionId, Long userId, String role) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new AppException("Prescription not found", HttpStatus.NOT_FOUND));

        if ("USER".equals(role) && !prescription.getPatientId().equals(userId)) {
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        }
        if ("DOCTOR".equals(role)) {
            Doctor doctor = doctorRepository.findByUserId(userId)
                    .orElseThrow(() -> new AppException("Doctor not found", HttpStatus.NOT_FOUND));
            if (!prescription.getDoctorId().equals(doctor.getId())) {
                throw new AppException("Access denied", HttpStatus.FORBIDDEN);
            }
        }

        User patient = userRepository.findById(prescription.getPatientId()).orElse(null);
        Doctor doctor = doctorRepository.findById(prescription.getDoctorId()).orElse(null);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            String docId = "RX-" + prescription.getId();
            String prescriptionDate = prescription.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

            PdfBrandingConfig branding = new PdfBrandingConfig(
                    "Medical Prescription",
                    "Digitally issued through Healix",
                    "Prescription No.",
                    docId,
                    docId
            );
            Document document = PdfBrandingHelper.openDocumentWithPageBranding(baos, branding);

            PdfBrandingHelper.addSectionTitle(document, "Doctor Information");
            Table doctorTable = PdfBrandingHelper.createInfoTable();
            PdfBrandingHelper.addInfoRow(doctorTable, "Doctor Name", doctor != null ? doctor.getName() : "N/A");
            PdfBrandingHelper.addInfoRow(doctorTable, "Specialization",
                    doctor != null ? doctor.getSpecialization().getName() : "N/A");
            PdfBrandingHelper.addInfoRow(doctorTable, "Hospital / Clinic", doctor != null ? doctor.getHospital() : "N/A");
            PdfBrandingHelper.addInfoRow(doctorTable, "License No.", doctor != null ? doctor.getLicenseNumber() : "N/A");
            document.add(doctorTable);

            PdfBrandingHelper.addSectionTitle(document, "Patient Information");
            Table patientTable = PdfBrandingHelper.createInfoTable();
            PdfBrandingHelper.addInfoRow(patientTable, "Patient Name",
                    patient != null ? patient.getFirstName() + " " + patient.getLastName() : "N/A");
            PdfBrandingHelper.addInfoRow(patientTable, "Prescription Date", prescriptionDate);
            PdfBrandingHelper.addInfoRow(patientTable, "Diagnosis", prescription.getDiagnosis());
            document.add(patientTable);

            PdfBrandingHelper.addContentBlock(document, "Prescribed Medicines", prescription.getMedicines());

            Div trailing = null;
            if ((prescription.getInstructions() != null && !prescription.getInstructions().isBlank())
                    || prescription.getFollowUpDate() != null) {
                trailing = new Div();
                if (prescription.getInstructions() != null && !prescription.getInstructions().isBlank()) {
                    trailing.add(PdfBrandingHelper.createContentBlock(
                            "Dosage & Instructions", prescription.getInstructions()));
                }
                if (prescription.getFollowUpDate() != null) {
                    trailing.add(PdfBrandingHelper.createContentBlock(
                            "Follow-up Appointment",
                            "Please revisit on " + prescription.getFollowUpDate() + " or as advised by your doctor."));
                }
            }

            PdfBrandingHelper.addClosingWithSignature(
                    document,
                    trailing,
                    "This prescription is issued electronically via the Healix platform (www.healix.in).",
                    doctor != null ? doctor.getName() : null
            );

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new AppException("Failed to generate prescription PDF", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String formatPossibleDiseases(String json) {
        if (json == null || json.isBlank()) {
            return "";
        }
        try {
            JsonNode node = objectMapper.readTree(json);
            StringBuilder builder = new StringBuilder();
            node.forEach(item -> {
                if (builder.length() > 0) {
                    builder.append("\n");
                }
                String name = item.get("name").asText();
                double confidence = item.get("confidence").asDouble() * 100;
                builder.append(String.format("• %s (%.1f%% match)", name, confidence));
            });
            return builder.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private String formatSymptom(String symptom) {
        return symptom.replace('_', ' ');
    }
}
