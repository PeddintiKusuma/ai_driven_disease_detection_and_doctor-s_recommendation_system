package com.aihealthcare.controller;

import com.aihealthcare.dto.request.AppointmentRequest;
import com.aihealthcare.dto.request.RescheduleAppointmentRequest;
import com.aihealthcare.dto.response.ApiResponse;
import com.aihealthcare.dto.response.AppointmentResponse;
import com.aihealthcare.dto.response.DoctorSlotAvailabilityResponse;
import com.aihealthcare.model.entity.Doctor;
import com.aihealthcare.model.entity.User;
import com.aihealthcare.model.enums.AppointmentStatus;
import com.aihealthcare.repository.DoctorRepository;
import com.aihealthcare.security.CustomUserDetailsService;
import com.aihealthcare.service.AppointmentService;
import com.aihealthcare.service.AppointmentSlotService;
import com.aihealthcare.service.PdfReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final PdfReportService pdfReportService;
    private final CustomUserDetailsService userDetailsService;
    private final DoctorRepository doctorRepository;
    private final AppointmentSlotService slotService;

    @GetMapping("/slots")
    public ResponseEntity<ApiResponse<DoctorSlotAvailabilityResponse>> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam String date,
            @RequestParam(required = false) Long excludeAppointmentId) {
        return ResponseEntity.ok(ApiResponse.success(
                slotService.getSlotAvailability(doctorId, LocalDate.parse(date), excludeAppointmentId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentResponse>> createAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AppointmentRequest request) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(
                "Appointment booked", appointmentService.createAppointment(user.getId(), request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getUserAppointments(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(appointmentService.getUserAppointments(user.getId())));
    }

    @PatchMapping("/{id}/reschedule")
    public ResponseEntity<ApiResponse<AppointmentResponse>> reschedule(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody RescheduleAppointmentRequest request) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Appointment rescheduled",
                appointmentService.rescheduleAppointment(
                        id, user.getId(), request.getAppointmentDate(), request.getAppointmentTime())));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        AppointmentStatus status = AppointmentStatus.valueOf(body.get("status"));
        return ResponseEntity.ok(ApiResponse.success(
                appointmentService.updateStatus(id, status, user.getId(), user.getRole().name())));
    }

    @GetMapping("/{id}/letter")
    public ResponseEntity<byte[]> downloadLetter(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        byte[] pdf = pdfReportService.generateAppointmentLetter(id, user.getId());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=appointment-letter-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
