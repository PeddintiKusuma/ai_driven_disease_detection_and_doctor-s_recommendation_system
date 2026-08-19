package com.aihealthcare.controller;

import com.aihealthcare.dto.request.ReviewRequest;
import com.aihealthcare.dto.response.ApiResponse;
import com.aihealthcare.model.entity.User;
import com.aihealthcare.security.CustomUserDetailsService;
import com.aihealthcare.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> submitReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ReviewRequest request) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(
                "Review submitted", reviewService.submitReview(user.getId(), request)));
    }
}
