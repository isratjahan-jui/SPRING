package com.MHM.MultiHotelManagement.dto.mapper;

import com.MHM.MultiHotelManagement.dto.request.HotelDetailsRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.HotelDetailsResponseDTO;
import com.MHM.MultiHotelManagement.entity.HotelDetails;

public class HotelDetailsMapper {

    // Entity → ResponseDTO
    public static HotelDetailsResponseDTO toDTO(HotelDetails details) {
        HotelDetailsResponseDTO dto = new HotelDetailsResponseDTO();
        dto.setId(details.getId());
        dto.setOwnerSpeach(details.getOwnerSpeach());
        dto.setDescription(details.getDescription());
        dto.setHotelPolicy(details.getHotelPolicy());
        dto.setPricePerNight(details.getPricePerNight());
        dto.setCheckInTime(details.getCheckInTime());
        dto.setCheckOutTime(details.getCheckOutTime());
        dto.setContactEmail(details.getContactEmail());
        dto.setContactPhone(details.getContactPhone());
        dto.setCancellationPolicy(details.getCancellationPolicy());
        dto.setPetPolicy(details.getPetPolicy());
        dto.setSmokingPolicy(details.getSmokingPolicy());
        dto.setChildPolicy(details.getChildPolicy());
        dto.setLanguages(details.getLanguages());
        dto.setNearbyAttractions(details.getNearbyAttractions());
        dto.setPaymentOption(details.getPaymentOption());
        dto.setDepositPercentage(details.getDepositPercentage());
        dto.setPreAuthRequired(details.getPreAuthRequired());
        dto.setCancellationDepositRefundable(details.getCancellationDepositRefundable());

        if (details.getHotel() != null) {
            dto.setHotelId(details.getHotel().getId());
            dto.setHotelName(details.getHotel().getHotelName());
        }
        return dto;
    }

    // RequestDTO → Entity
    public static HotelDetails toEntity(HotelDetailsRequestDTO dto) {
        HotelDetails details = new HotelDetails();
        details.setOwnerSpeach(dto.getOwnerSpeach());
        details.setDescription(dto.getDescription());
        details.setHotelPolicy(dto.getHotelPolicy());
        details.setPricePerNight(dto.getPricePerNight());
        details.setCheckInTime(dto.getCheckInTime());
        details.setCheckOutTime(dto.getCheckOutTime());
        details.setContactEmail(dto.getContactEmail());
        details.setContactPhone(dto.getContactPhone());
        details.setCancellationPolicy(dto.getCancellationPolicy());
        details.setPetPolicy(dto.getPetPolicy());
        details.setSmokingPolicy(dto.getSmokingPolicy());
        details.setChildPolicy(dto.getChildPolicy());
        details.setLanguages(dto.getLanguages());
        details.setNearbyAttractions(dto.getNearbyAttractions());
        details.setPaymentOption(dto.getPaymentOption());
        details.setDepositPercentage(dto.getDepositPercentage());
        details.setPreAuthRequired(dto.getPreAuthRequired());
        details.setCancellationDepositRefundable(dto.getCancellationDepositRefundable());
        return details;
    }
}
