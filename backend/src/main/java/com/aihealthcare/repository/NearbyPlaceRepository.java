package com.aihealthcare.repository;

import com.aihealthcare.model.entity.NearbyPlace;
import com.aihealthcare.model.enums.PlaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NearbyPlaceRepository extends JpaRepository<NearbyPlace, Long> {
    List<NearbyPlace> findByCityIgnoreCase(String city);
    List<NearbyPlace> findByCityIgnoreCaseAndType(String city, PlaceType type);
}
