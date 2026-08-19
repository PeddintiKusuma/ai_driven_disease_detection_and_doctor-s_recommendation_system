package com.aihealthcare.controller;

import com.aihealthcare.dto.request.PrescriptionRequest;
import com.aihealthcare.dto.response.ApiResponse;
import com.aihealthcare.dto.response.PrescriptionResponse;
import com.aihealthcare.model.entity.User;
import com.aihealthcare.security.CustomUserDetailsService;
import com.aihealthcare.service.PdfReportService;
import com.aihealthcare.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final PdfReportService pdfReportService;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping
    public ResponseEntity<ApiResponse<PrescriptionResponse>> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PrescriptionRequest request) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(
                "Prescription created", prescriptionService.createPrescription(user.getId(), request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PrescriptionResponse>>> getMyPrescriptions(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        List<PrescriptionResponse> list = "DOCTOR".equals(user.getRole().name())
                ? prescriptionService.getDoctorPrescriptions(user.getId())
                : prescriptionService.getPatientPrescriptions(user.getId());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        byte[] pdf = pdfReportService.generatePrescriptionPdf(id, user.getId(), user.getRole().name());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=prescription-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
