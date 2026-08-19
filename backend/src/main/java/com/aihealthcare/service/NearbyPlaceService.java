package com.aihealthcare.service;

import com.aihealthcare.dto.response.NearbyPlaceResponse;
import com.aihealthcare.model.entity.NearbyPlace;
import com.aihealthcare.model.enums.PlaceType;
import com.aihealthcare.repository.NearbyPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NearbyPlaceService {

    private final NearbyPlaceRepository nearbyPlaceRepository;

    public List<NearbyPlaceResponse> getNearby(String city, PlaceType type) {
        List<NearbyPlace> places = type != null
                ? nearbyPlaceRepository.findByCityIgnoreCaseAndType(city, type)
                : nearbyPlaceRepository.findByCityIgnoreCase(city);

        if (places.isEmpty()) {
            places = nearbyPlaceRepository.findAll();
        }

        return places.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<NearbyPlaceResponse> getAll() {
        return nearbyPlaceRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    private NearbyPlaceResponse toResponse(NearbyPlace p) {
        return NearbyPlaceResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .type(p.getType())
                .address(p.getAddress())
                .city(p.getCity())
                .phone(p.getPhone())
                .openingHours(p.getOpeningHours())
                .latitude(p.getLatitude())
                .longitude(p.getLongitude())
                .build();
    }
}
