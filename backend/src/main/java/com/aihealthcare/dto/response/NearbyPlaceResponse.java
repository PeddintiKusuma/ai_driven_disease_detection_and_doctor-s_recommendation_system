package com.aihealthcare.dto.response;

import com.aihealthcare.model.enums.PlaceType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NearbyPlaceResponse {
    private Long id;
    private String name;
    private PlaceType type;
    private String address;
    private String city;
    private String phone;
    private String openingHours;
    private Double latitude;
    private Double longitude;
}
