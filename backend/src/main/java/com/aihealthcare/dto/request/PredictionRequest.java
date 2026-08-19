package com.aihealthcare.dto.request;

import com.aihealthcare.model.enums.Gender;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class PredictionRequest {
    private Integer age;
    private Gender gender;

    @NotEmpty(message = "At least one symptom is required")
    @Size(min = 1, max = 20, message = "Provide between 1 and 20 symptoms")
    private List<String> symptoms;
}
