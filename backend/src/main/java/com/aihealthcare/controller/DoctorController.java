package com.aihealthcare.controller;

import com.aihealthcare.dto.response.ApiResponse;
import com.aihealthcare.dto.response.DoctorResponse;
import com.aihealthcare.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DoctorResponse>>> getAllDoctors() {
        return ResponseEntity.ok(ApiResponse.success(doctorService.getAllDoctors()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDoctorProfile(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(doctorService.getDoctorProfile(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DoctorResponse>>> searchDoctors(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) BigDecimal minFee,
            @RequestParam(required = false) BigDecimal maxFee,
            @RequestParam(required = false) Double minRating) {
        if (specialization != null && q == null && city == null) {
            return ResponseEntity.ok(ApiResponse.success(doctorService.searchBySpecialization(specialization)));
        }
        return ResponseEntity.ok(ApiResponse.success(
                doctorService.searchDoctors(q, specialization, city, minFee, maxFee, minRating)));
    }
}
