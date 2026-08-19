package com.aihealthcare.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DoctorScheduleUpdateRequest {
    @NotBlank
    private String availableDays;

    @NotBlank
    private String availableTime;
}
