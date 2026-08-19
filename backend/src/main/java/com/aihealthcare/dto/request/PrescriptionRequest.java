package com.aihealthcare.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrescriptionRequest {
    @NotNull private Long appointmentId;
    private String diagnosis;
    @NotBlank private String medicines;
    private String instructions;
    private String followUpDate;
}
