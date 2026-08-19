package com.aihealthcare.service;

import com.aihealthcare.dto.request.ReviewRequest;
import com.aihealthcare.exception.AppException;
import com.aihealthcare.model.entity.Appointment;
import com.aihealthcare.model.entity.Doctor;
import com.aihealthcare.model.entity.Review;
import com.aihealthcare.model.enums.AppointmentStatus;
import com.aihealthcare.repository.AppointmentRepository;
import com.aihealthcare.repository.DoctorRepository;
import com.aihealthcare.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final NotificationService notificationService;

    @Transactional
    public Map<String, Object> submitReview(Long userId, ReviewRequest request) {
        if (reviewRepository.existsByUserIdAndAppointmentId(userId, request.getAppointmentId())) {
            throw new AppException("You have already reviewed this appointment", HttpStatus.CONFLICT);
        }

        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new AppException("Appointment not found", HttpStatus.NOT_FOUND));

        if (!appointment.getUserId().equals(userId)) {
            throw new AppException("Access denied", HttpStatus.FORBIDDEN);
        }
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new AppException("You can only review completed appointments", HttpStatus.BAD_REQUEST);
        }
        if (!appointment.getDoctor().getId().equals(request.getDoctorId())) {
            throw new AppException("Doctor mismatch", HttpStatus.BAD_REQUEST);
        }

        Review review = Review.builder()
                .userId(userId)
                .doctor(appointment.getDoctor())
                .appointmentId(request.getAppointmentId())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
        reviewRepository.save(review);

        recalculateDoctorRating(request.getDoctorId());

        Doctor doctor = appointment.getDoctor();
        if (doctor.getUserId() != null) {
            notificationService.notify(doctor.getUserId(), "New Review",
                    "A patient left a " + request.getRating() + "-star review.", "REVIEW");
        }

        return Map.of("id", review.getId(), "rating", review.getRating(), "message", "Review submitted");
    }

    private void recalculateDoctorRating(Long doctorId) {
        List<Review> reviews = reviewRepository.findByDoctorIdOrderByCreatedAtDesc(doctorId);
        if (reviews.isEmpty()) return;

        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0);
        doctorRepository.findById(doctorId).ifPresent(doctor -> {
            doctor.setRating(BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
            doctorRepository.save(doctor);
        });
    }
}
