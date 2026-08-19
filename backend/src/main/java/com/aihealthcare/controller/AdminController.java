package com.aihealthcare.controller;

import com.aihealthcare.dto.response.*;
import com.aihealthcare.model.entity.User;
import com.aihealthcare.security.CustomUserDetailsService;
import com.aihealthcare.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final DoctorService doctorService;
    private final CustomUserDetailsService userDetailsService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminStatsResponse>> getStats() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getStats()));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsers() {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers()));
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse<Void>> setUserStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        User admin = userDetailsService.getUserByEmail(userDetails.getUsername());
        adminService.setUserStatus(id, body.getOrDefault("enabled", true), admin.getId());
        return ResponseEntity.ok(ApiResponse.success("User status updated", null));
    }

    @GetMapping("/doctors")
    public ResponseEntity<ApiResponse<List<DoctorResponse>>> getDoctors() {
        return ResponseEntity.ok(ApiResponse.success(doctorService.getAllDoctorsAdmin()));
    }

    @PatchMapping("/doctors/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approveDoctor(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        User admin = userDetailsService.getUserByEmail(userDetails.getUsername());
        adminService.approveDoctor(id, admin.getId());
        return ResponseEntity.ok(ApiResponse.success("Doctor approved", null));
    }

    @PatchMapping("/doctors/{id}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectDoctor(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        User admin = userDetailsService.getUserByEmail(userDetails.getUsername());
        adminService.rejectDoctor(id, admin.getId());
        return ResponseEntity.ok(ApiResponse.success("Doctor rejected", null));
    }

    @GetMapping("/predictions")
    public ResponseEntity<ApiResponse<List<PredictionResponse>>> getAllPredictions() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getAllPredictions()));
    }

    @GetMapping("/appointments")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAllAppointments() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getAllAppointments()));
    }
}
