package com.aihealthcare.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class TimeSlotResponse {
    private LocalTime time;
    private String label;
    private boolean available;
}
