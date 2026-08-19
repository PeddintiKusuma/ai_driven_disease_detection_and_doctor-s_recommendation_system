package com.aihealthcare.controller;

import com.aihealthcare.dto.request.DoctorProfileUpdateRequest;
import com.aihealthcare.dto.request.DoctorScheduleUpdateRequest;
import com.aihealthcare.dto.response.ApiResponse;
import com.aihealthcare.dto.response.AppointmentResponse;
import com.aihealthcare.dto.response.DoctorResponse;
import com.aihealthcare.model.entity.User;
import com.aihealthcare.security.CustomUserDetailsService;
import com.aihealthcare.service.AppointmentService;
import com.aihealthcare.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorDashboardController {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final CustomUserDetailsService userDetailsService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<DoctorResponse>> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(doctorService.getProfileByUserId(user.getId())));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<DoctorResponse>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody DoctorProfileUpdateRequest request) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(doctorService.updateProfile(user.getId(), request)));
    }

    @PutMapping("/schedule")
    public ResponseEntity<ApiResponse<DoctorResponse>> updateSchedule(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody DoctorScheduleUpdateRequest request) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(doctorService.updateSchedule(user.getId(), request)));
    }

    @GetMapping("/appointments")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getDoctorAppointments(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        var doctor = doctorService.getDoctorByUserId(user.getId());
        return ResponseEntity.ok(ApiResponse.success(appointmentService.getDoctorAppointments(doctor.getId())));
    }

    @PatchMapping("/appointments/{id}/accept")
    public ResponseEntity<ApiResponse<AppointmentResponse>> acceptAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(
                appointmentService.updateStatus(id, com.aihealthcare.model.enums.AppointmentStatus.CONFIRMED, user.getId(), "DOCTOR")));
    }

    @PatchMapping("/appointments/{id}/reject")
    public ResponseEntity<ApiResponse<AppointmentResponse>> rejectAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(
                appointmentService.updateStatus(id, com.aihealthcare.model.enums.AppointmentStatus.CANCELLED, user.getId(), "DOCTOR")));
    }

    @PatchMapping("/appointments/{id}/complete")
    public ResponseEntity<ApiResponse<AppointmentResponse>> completeAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(
                appointmentService.updateStatus(id, com.aihealthcare.model.enums.AppointmentStatus.COMPLETED, user.getId(), "DOCTOR")));
    }

    @PatchMapping("/appointments/{id}/uncomplete")
    public ResponseEntity<ApiResponse<AppointmentResponse>> uncompleteAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(appointmentService.revertCompletion(id, user.getId())));
    }
}
