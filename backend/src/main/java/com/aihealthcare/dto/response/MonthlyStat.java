package com.aihealthcare.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyStat {
    private int month;
    private long count;
}
