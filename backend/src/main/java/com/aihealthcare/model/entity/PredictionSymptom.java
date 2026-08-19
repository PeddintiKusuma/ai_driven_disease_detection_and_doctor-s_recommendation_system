package com.aihealthcare.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prediction_symptoms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionSymptom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prediction_id", nullable = false)
    private Prediction prediction;

    @Column(name = "symptom_name", nullable = false)
    private String symptomName;
}
