package com.aihealthcare.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "diet_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DietPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "disease_name", nullable = false, unique = true)
    private String diseaseName;

    @Column(columnDefinition = "TEXT")
    private String dos;

    @Column(name = "donts", columnDefinition = "TEXT")
    private String donts;

    @Column(name = "diet_recommendations", columnDefinition = "TEXT")
    private String dietRecommendations;

    @Column(name = "general_advice", columnDefinition = "TEXT")
    private String generalAdvice;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
