package com.aihealthcare.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DoctorRegisterRequest {
    @NotBlank private String email;
    @NotBlank private String password;
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    private String phone;
    @NotBlank private String specialization;
    @NotBlank private String qualification;
    @NotNull private Integer experience;
    @NotNull private BigDecimal consultationFee;
    @NotBlank private String hospital;
    @NotBlank private String city;
    @NotBlank private String availableDays;
    @NotBlank private String availableTime;
    private String bio;
    private String licenseNumber;
}
