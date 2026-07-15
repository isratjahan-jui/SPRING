package com.MHM.MultiHotelManagement.dto.mapper;



import com.MHM.MultiHotelManagement.dto.response.DealsResponseDTO;
import com.MHM.MultiHotelManagement.entity.Deals;

public class DealsMapper {

    public static DealsResponseDTO toDTO(Deals deals) {
        DealsResponseDTO dto = new DealsResponseDTO();
        dto.setId(deals.getId());
        dto.setDealTitle(deals.getDealTitle());
        dto.setDescription(deals.getDescription());
        dto.setDiscountPercent(deals.getDiscountPercent());
        dto.setDiscountAmount(deals.getDiscountAmount());
        dto.setStartDate(deals.getStartDate());
        dto.setEndDate(deals.getEndDate());
        dto.setDealType(deals.getDealType());
        dto.setHotelName(deals.getHotel().getHotelName());
        dto.setRoomType(deals.getRoom() != null ? deals.getRoom().getRoomType() : null);
        dto.setIsActive(deals.getIsActive());
        return dto;
    }
}
