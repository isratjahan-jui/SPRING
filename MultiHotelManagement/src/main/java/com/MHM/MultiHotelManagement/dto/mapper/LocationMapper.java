package com.MHM.MultiHotelManagement.dto.mapper;

import com.MHM.MultiHotelManagement.dto.request.LocationRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.LocationResponseDTO;
import com.MHM.MultiHotelManagement.entity.Location;

import java.util.Collections;
import java.util.stream.Collectors;

public class LocationMapper {

    public static LocationResponseDTO toDTO(Location location) {
        LocationResponseDTO dto = new LocationResponseDTO();

        dto.setId(location.getId());
        dto.setLocationName(location.getLocationName());
        dto.setLocationImage(location.getLocationImage());
        dto.setCity(location.getCity());
        dto.setDistrict(location.getDistrict());
        dto.setDivision(location.getDivision());
        dto.setUpazila(location.getUpazila());
        dto.setCreatedAt(location.getCreatedAt());

        dto.setTotalHotels(
                location.getHotels() != null
                        ? location.getHotels().size()
                        : 0
        );

        return dto;
    }

    public static LocationResponseDTO toDTOWithHotels(
            Location location
    ) {
        LocationResponseDTO dto = toDTO(location);

        if (location.getHotels() != null
                && !location.getHotels().isEmpty()) {
            dto.setHotels(
                    location.getHotels()
                            .stream()
                            .map(hotel -> {
                                LocationResponseDTO.HotelBasicInfo
                                        info = new LocationResponseDTO
                                        .HotelBasicInfo();
                                info.setId(hotel.getId());
                                info.setName(hotel.getHotelName());
                                info.setPricePerNight(
                                        hotel.getPricePerNight()
                                );
                                info.setRating(hotel.getRating());
                                info.setStatus(
                                        hotel.getStatus() != null
                                                ? hotel.getStatus().name()
                                                : null
                                );
                                return info;
                            })
                            .collect(Collectors.toList())
            );
        } else {
            dto.setHotels(Collections.emptyList());
        }

        return dto;
    }

    public static Location toEntity(LocationRequestDTO dto) {
        Location location = new Location();
        location.setLocationName(dto.getLocationName());
        location.setCity(dto.getCity());
        location.setDistrict(dto.getDistrict());
        location.setDivision(dto.getDivision());
        location.setUpazila(dto.getUpazila());
        return location;
    }
}
