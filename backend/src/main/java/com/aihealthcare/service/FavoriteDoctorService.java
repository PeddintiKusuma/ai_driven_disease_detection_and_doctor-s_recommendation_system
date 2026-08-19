package com.aihealthcare.service;

import com.aihealthcare.dto.response.DoctorResponse;
import com.aihealthcare.exception.AppException;
import com.aihealthcare.model.entity.Doctor;
import com.aihealthcare.model.entity.FavoriteDoctor;
import com.aihealthcare.repository.DoctorRepository;
import com.aihealthcare.repository.FavoriteDoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteDoctorService {

    private final FavoriteDoctorRepository favoriteDoctorRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorRecommendationService recommendationService;

    public List<DoctorResponse> getFavorites(Long userId) {
        return favoriteDoctorRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(f -> recommendationService.toDoctorResponse(f.getDoctor()))
                .toList();
    }

    @Transactional
    public void addFavorite(Long userId, Long doctorId) {
        if (favoriteDoctorRepository.existsByUserIdAndDoctorId(userId, doctorId)) {
            throw new AppException("Doctor already in favorites", HttpStatus.CONFLICT);
        }
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new AppException("Doctor not found", HttpStatus.NOT_FOUND));
        favoriteDoctorRepository.save(FavoriteDoctor.builder()
                .userId(userId)
                .doctor(doctor)
                .build());
    }

    @Transactional
    public void removeFavorite(Long userId, Long doctorId) {
        favoriteDoctorRepository.findByUserIdAndDoctorId(userId, doctorId)
                .ifPresent(favoriteDoctorRepository::delete);
    }
}
