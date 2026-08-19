package com.aihealthcare.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PossibleDiseaseDto {
    private String name;
    private BigDecimal confidence;
}
