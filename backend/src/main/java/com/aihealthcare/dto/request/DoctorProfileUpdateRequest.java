package com.aihealthcare.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DoctorProfileUpdateRequest {
    private String phone;
    private String qualification;
    private String hospital;
    private String city;
    private String bio;
    private BigDecimal consultationFee;
}
