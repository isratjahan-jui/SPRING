package com.MHM.MultiHotelManagement.dto.mapper;

import com.MHM.MultiHotelManagement.dto.request.HotelExtraServiceRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.HotelExtraServiceResponseDTO;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.entity.HotelExtraService;

public class HotelExtraServiceMapper {

    public static HotelExtraService toEntity(HotelExtraServiceRequestDTO dto) {
        HotelExtraService entity = new HotelExtraService();
        entity.setServiceName(dto.getServiceName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        return entity;
    }

    public static HotelExtraServiceResponseDTO toResponseDTO(HotelExtraService entity) {
        HotelExtraServiceResponseDTO dto = new HotelExtraServiceResponseDTO();
        dto.setId(entity.getId());
        dto.setServiceName(entity.getServiceName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setIsActive(entity.getIsActive());
        Hotel hotel = entity.getHotel();
        if (hotel != null) {
            dto.setHotelId(hotel.getId());
            dto.setHotelName(hotel.getHotelName());
        }
        return dto;
    }
}
