package com.MHM.MultiHotelManagement.dto.mapper;

import com.MHM.MultiHotelManagement.dto.request.FacilityRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.FacilityResponseDTO;
import com.MHM.MultiHotelManagement.entity.Facility;

public class FacilityMapper {

    // Entity → ResponseDTO
    public static FacilityResponseDTO toDTO(Facility facility) {
        FacilityResponseDTO dto = new FacilityResponseDTO();

        dto.setId(facility.getId());
        dto.setFacilityName(facility.getFacilityName());
        dto.setDescription(facility.getDescription());
        dto.setCreatedAt(facility.getCreatedAt());
        dto.setUpdatedAt(facility.getUpdatedAt());

        // Hotel info
        if (facility.getHotel() != null) {
            dto.setHotelId(facility.getHotel().getId());
            dto.setHotelName(facility.getHotel().getHotelName());
        }

        return dto;
    }

    // RequestDTO → Entity
    public static Facility toEntity(FacilityRequestDTO dto) {
        Facility facility = new Facility();
        facility.setFacilityName(dto.getFacilityName());
        facility.setDescription(dto.getDescription());
        return facility;
    }
}