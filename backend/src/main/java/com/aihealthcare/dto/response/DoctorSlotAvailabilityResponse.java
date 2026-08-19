package com.aihealthcare.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DoctorSlotAvailabilityResponse {
    private List<TimeSlotResponse> slots;
    private int bookedCount;
    private int dailyLimit;
    private boolean dailyLimitReached;
    private boolean doctorAvailableOnDate;
    private boolean scheduleConfigured;
    private String availableDays;
    private String availableTime;
    private String message;
}
