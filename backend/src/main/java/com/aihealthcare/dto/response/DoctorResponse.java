package com.aihealthcare.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DoctorResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String specialization;
    private String qualification;
    private Integer experience;
    private BigDecimal rating;
    private BigDecimal consultationFee;
    private String hospital;
    private String city;
    private String availableDays;
    private String availableTime;
    private String bio;
    private Double recommendationScore;
    private String approvalStatus;
    private String licenseNumber;
    private long reviewCount;
}
