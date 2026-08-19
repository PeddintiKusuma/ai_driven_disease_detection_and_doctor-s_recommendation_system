package com.aihealthcare.dto.response;

import com.aihealthcare.model.enums.AppointmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
public class AppointmentResponse {
    private Long id;
    private Long userId;
    private String userName;
    private DoctorResponse doctor;
    private Long predictionId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String reason;
    private String notes;
    private AppointmentStatus status;
    private LocalDateTime createdAt;
}
