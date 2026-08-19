package com.aihealthcare.controller;

import com.aihealthcare.dto.response.ApiResponse;
import com.aihealthcare.dto.response.DoctorResponse;
import com.aihealthcare.model.entity.User;
import com.aihealthcare.security.CustomUserDetailsService;
import com.aihealthcare.service.FavoriteDoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteDoctorController {

    private final FavoriteDoctorService favoriteDoctorService;
    private final CustomUserDetailsService userDetailsService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DoctorResponse>>> getFavorites(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(favoriteDoctorService.getFavorites(user.getId())));
    }

    @PostMapping("/{doctorId}")
    public ResponseEntity<ApiResponse<Void>> addFavorite(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long doctorId) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        favoriteDoctorService.addFavorite(user.getId(), doctorId);
        return ResponseEntity.ok(ApiResponse.success("Added to favorites", null));
    }

    @DeleteMapping("/{doctorId}")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long doctorId) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        favoriteDoctorService.removeFavorite(user.getId(), doctorId);
        return ResponseEntity.ok(ApiResponse.success("Removed from favorites", null));
    }
}
