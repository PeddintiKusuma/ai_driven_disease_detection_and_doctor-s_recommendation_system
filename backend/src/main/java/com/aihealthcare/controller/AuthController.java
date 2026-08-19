package com.aihealthcare.controller;

import com.aihealthcare.dto.request.AdminRegisterRequest;
import com.aihealthcare.dto.request.DoctorRegisterRequest;
import com.aihealthcare.dto.request.LoginRequest;
import com.aihealthcare.dto.request.RegisterRequest;
import com.aihealthcare.dto.response.ApiResponse;
import com.aihealthcare.dto.response.AuthResponse;
import com.aihealthcare.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Registration successful", authService.register(request)));
    }

    @PostMapping("/register-doctor")
    public ResponseEntity<ApiResponse<AuthResponse>> registerDoctor(@Valid @RequestBody DoctorRegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Doctor registration successful", authService.registerDoctor(request)));
    }

    @PostMapping("/register-admin")
    public ResponseEntity<ApiResponse<AuthResponse>> registerAdmin(@Valid @RequestBody AdminRegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Admin registration successful", authService.registerAdmin(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Login successful", authService.login(request)));
    }
}
