package com.aihealthcare.service;

import com.aihealthcare.dto.request.AdminRegisterRequest;
import com.aihealthcare.dto.request.DoctorRegisterRequest;
import com.aihealthcare.dto.request.LoginRequest;
import com.aihealthcare.dto.request.RegisterRequest;
import com.aihealthcare.dto.response.AuthResponse;
import com.aihealthcare.dto.response.UserResponse;
import com.aihealthcare.exception.AppException;
import com.aihealthcare.model.entity.Doctor;
import com.aihealthcare.model.entity.Specialization;
import com.aihealthcare.model.entity.User;
import com.aihealthcare.model.enums.DoctorApprovalStatus;
import com.aihealthcare.model.enums.Role;
import com.aihealthcare.repository.DoctorRepository;
import com.aihealthcare.repository.SpecializationRepository;
import com.aihealthcare.repository.UserRepository;
import com.aihealthcare.security.CustomUserDetailsService;
import com.aihealthcare.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @Value("${app.admin.registration-key:Healix@Admin2026}")
    private String adminRegistrationKey;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email already registered", HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .age(request.getAge())
                .gender(request.getGender())
                .role(Role.USER)
                .enabled(true)
                .build();

        user = userRepository.save(user);
        String token = generateToken(user);

        return AuthResponse.builder()
                .success(true)
                .token(token)
                .user(toUserResponse(user))
                .message("Registration successful")
                .build();
    }

    public AuthResponse registerDoctor(DoctorRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email already registered", HttpStatus.CONFLICT);
        }

        Specialization spec = specializationRepository.findByName(request.getSpecialization())
                .orElseGet(() -> specializationRepository.save(
                        Specialization.builder().name(request.getSpecialization())
                                .description(request.getSpecialization() + " specialist").build()));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role(Role.DOCTOR)
                .enabled(true)
                .build();
        user = userRepository.save(user);

        Doctor doctor = Doctor.builder()
                .userId(user.getId())
                .name("Dr. " + request.getFirstName() + " " + request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .specialization(spec)
                .qualification(request.getQualification())
                .experience(request.getExperience())
                .rating(BigDecimal.valueOf(0))
                .consultationFee(request.getConsultationFee())
                .hospital(request.getHospital())
                .city(request.getCity())
                .availableDays(request.getAvailableDays())
                .availableTime(request.getAvailableTime())
                .bio(request.getBio())
                .licenseNumber(request.getLicenseNumber())
                .approvalStatus(DoctorApprovalStatus.PENDING)
                .build();
        doctorRepository.save(doctor);

        auditLogService.log(user.getId(), "DOCTOR_REGISTERED", "DOCTOR", doctor.getId(),
                "Doctor registration pending approval: " + doctor.getName());

        String token = generateToken(user);
        return AuthResponse.builder()
                .success(true)
                .token(token)
                .user(toUserResponse(user))
                .message("Doctor registration submitted. Your profile is pending admin approval.")
                .build();
    }

    public AuthResponse registerAdmin(AdminRegisterRequest request) {
        if (!adminRegistrationKey.equals(request.getRegistrationKey())) {
            throw new AppException("Invalid admin registration key", HttpStatus.FORBIDDEN);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email already registered", HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role(Role.ADMIN)
                .enabled(true)
                .build();
        user = userRepository.save(user);

        auditLogService.log(user.getId(), "ADMIN_REGISTERED", "USER", user.getId(),
                "New admin account created: " + user.getEmail());

        String token = generateToken(user);
        return AuthResponse.builder()
                .success(true)
                .token(token)
                .user(toUserResponse(user))
                .message("Admin registration successful")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userDetailsService.getUserByEmail(request.getEmail());
        if (!user.getEnabled()) {
            throw new AppException("Account is disabled. Contact admin.", HttpStatus.FORBIDDEN);
        }
        String token = generateToken(user);

        return AuthResponse.builder()
                .success(true)
                .token(token)
                .user(toUserResponse(user))
                .message("Login successful")
                .build();
    }

    private String generateToken(User user) {
        var userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        return jwtUtil.generateToken(userDetails, user.getId(), user.getRole().name());
    }

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .age(user.getAge())
                .gender(user.getGender())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .build();
    }
}
