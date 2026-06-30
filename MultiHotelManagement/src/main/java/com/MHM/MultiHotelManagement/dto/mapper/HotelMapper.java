package com.MHM.MultiHotelManagement.mapper;

import com.MHM.MultiHotelManagement.dto.request.HotelRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.HotelResponseDTO;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.enums.HotelStatus;

public class HotelMapper {

    public static HotelResponseDTO toDTO(Hotel hotel) {
        HotelResponseDTO dto = new HotelResponseDTO();
        dto.setId(hotel.getId());
        dto.setHotelName(hotel.getHotelName());
        dto.setAddress(hotel.getAddress());
        dto.setDescription(hotel.getDescription());
        dto.setRating(hotel.getRating());
        dto.setImage(hotel.getImage());
        dto.setStatus(hotel.getStatus().name());
        dto.setLocationId(hotel.getLocation() != null ? hotel.getLocation().getId() : null);
        dto.setOwnerId(hotel.getOwner() != null ? hotel.getOwner().getId() : null);

        // নতুন ফিল্ড সেট করা
        dto.setLocationName(hotel.getLocation() != null ? hotel.getLocation().getName() : null);
        dto.setOwnerName(hotel.getOwner() != null ? hotel.getOwner().getName() : null);

        return dto;
    }

    public static Hotel toEntity(HotelRequestDTO dto) {
        Hotel hotel = new Hotel();
        hotel.setHotelName(dto.getHotelName());
        hotel.setAddress(dto.getAddress());
        hotel.setDescription(dto.getDescription());
        hotel.setRating(dto.getRating());
        hotel.setImage(dto.getImage());
        hotel.setStatus(HotelStatus.valueOf(dto.getStatus()));
        return hotel;
    }
}
