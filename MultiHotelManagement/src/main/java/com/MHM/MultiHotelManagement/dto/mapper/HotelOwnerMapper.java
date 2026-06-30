package com.MHM.MultiHotelManagement.mapper;

import com.MHM.MultiHotelManagement.entity.HotelOwner;
import com.MHM.MultiHotelManagement.dto.request.HotelOwnerRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.HotelOwnerResponseDTO;

public class HotelOwnerMapper {

    public static HotelOwnerResponseDTO toDTO(HotelOwner owner) {
        HotelOwnerResponseDTO dto = new HotelOwnerResponseDTO();
        dto.setId(owner.getId());
        dto.setName(owner.getName());
        dto.setEmail(owner.getEmail());
        dto.setPhone(owner.getPhone());
        dto.setAddress(owner.getAddress());
        dto.setGender(owner.getGender());
        dto.setDateOfBirth(owner.getDateOfBirth());
        dto.setImage(owner.getImage());
        dto.setUserId(owner.getUser().getId());
        dto.setCreatedAt(owner.getCreatedAt());
        dto.setUpdatedAt(owner.getUpdatedAt());
        return dto;
    }

    public static HotelOwner toEntity(HotelOwnerRequestDTO dto) {
        HotelOwner owner = new HotelOwner();
        owner.setName(dto.getName());
        owner.setEmail(dto.getEmail());
        owner.setPhone(dto.getPhone());
        owner.setAddress(dto.getAddress());
        owner.setGender(dto.getGender());
        owner.setDateOfBirth(dto.getDateOfBirth());
        owner.setImage(dto.getImage());
        return owner;
    }
}
