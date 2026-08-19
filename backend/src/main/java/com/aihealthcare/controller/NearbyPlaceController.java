package com.aihealthcare.controller;

import com.aihealthcare.dto.response.ApiResponse;
import com.aihealthcare.dto.response.NearbyPlaceResponse;
import com.aihealthcare.model.enums.PlaceType;
import com.aihealthcare.service.NearbyPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class NearbyPlaceController {

    private final NearbyPlaceService nearbyPlaceService;

    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<NearbyPlaceResponse>>> getNearby(
            @RequestParam(defaultValue = "Hyderabad") String city,
            @RequestParam(required = false) PlaceType type) {
        return ResponseEntity.ok(ApiResponse.success(nearbyPlaceService.getNearby(city, type)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NearbyPlaceResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(nearbyPlaceService.getAll()));
    }
}
