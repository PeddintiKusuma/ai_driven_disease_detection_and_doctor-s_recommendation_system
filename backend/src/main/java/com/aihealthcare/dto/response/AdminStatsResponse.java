package com.aihealthcare.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class AdminStatsResponse {
    private long totalUsers;
    private long totalDoctors;
    private long totalPredictions;
    private long totalAppointments;
    private long pendingAppointments;
    private long pendingDoctors;
    private List<Map<String, Object>> predictionsByDisease;
    private List<MonthlyStat> monthlyPredictions;
    private List<MonthlyStat> monthlyRegistrations;
}
