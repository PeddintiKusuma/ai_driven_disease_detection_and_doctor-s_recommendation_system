package com.aihealthcare.controller;

import com.aihealthcare.dto.request.PredictionRequest;
import com.aihealthcare.dto.response.ApiResponse;
import com.aihealthcare.dto.response.PredictionResponse;
import com.aihealthcare.model.entity.User;
import com.aihealthcare.security.CustomUserDetailsService;
import com.aihealthcare.service.PdfReportService;
import com.aihealthcare.service.PredictionService;
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
@RequestMapping("/api/predictions")
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService predictionService;
    private final PdfReportService pdfReportService;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping
    public ResponseEntity<ApiResponse<PredictionResponse>> predict(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PredictionRequest request) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        PredictionResponse response = predictionService.createPrediction(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Prediction completed", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PredictionResponse>>> getHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(predictionService.getUserPredictions(user.getId())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PredictionResponse>> getPrediction(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(predictionService.getPrediction(id, user.getId())));
    }

    @GetMapping("/{id}/report")
    public ResponseEntity<byte[]> downloadReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        byte[] pdf = pdfReportService.generatePredictionReport(id, user.getId());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=prediction-report-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
