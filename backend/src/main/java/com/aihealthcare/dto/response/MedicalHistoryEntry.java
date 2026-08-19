package com.aihealthcare.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class MedicalHistoryEntry {
    private LocalDate date;
    private String type;
    private String description;
    private String doctorName;
    private Long referenceId;
}
