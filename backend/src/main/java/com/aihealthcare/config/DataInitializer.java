package com.aihealthcare.config;

import com.aihealthcare.model.entity.Specialization;
import com.aihealthcare.repository.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    @Profile("!test")
    CommandLineRunner initSpecializations(SpecializationRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                String[] specs = {
                    "General Physician", "Cardiologist", "Dermatologist", "Pulmonologist",
                    "Gastroenterologist", "Neurologist", "Nephrologist", "Ophthalmologist",
                    "ENT Specialist", "Psychiatrist", "Endocrinologist", "Orthopedic",
                    "Infectious Disease Specialist"
                };
                for (String name : specs) {
                    repo.save(Specialization.builder().name(name).description(name + " specialist").build());
                }
            }
        };
    }
}
