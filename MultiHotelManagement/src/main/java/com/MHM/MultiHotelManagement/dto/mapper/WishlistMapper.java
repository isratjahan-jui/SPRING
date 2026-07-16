package com.MHM.MultiHotelManagement.dto.mapper;

import com.MHM.MultiHotelManagement.dto.response.WishlistResponseDTO;
import com.MHM.MultiHotelManagement.entity.Wishlist;

public class WishlistMapper {

    public static WishlistResponseDTO toDTO(Wishlist wishlist) {
        WishlistResponseDTO dto = new WishlistResponseDTO();
        dto.setId(wishlist.getId());
        dto.setNotes(wishlist.getNotes());
        dto.setIsActive(wishlist.getIsActive());

        if (wishlist.getUser() != null) {
            dto.setUserId(wishlist.getUser().getId());
            dto.setUserName(wishlist.getUser().getName());
        }

        if (wishlist.getCustomer() != null) {
            dto.setCustomerId(wishlist.getCustomer().getId());
            dto.setCustomerName(wishlist.getCustomer().getCustomerName());
        }

        if (wishlist.getHotel() != null) {
            dto.setHotelId(wishlist.getHotel().getId());
            dto.setHotelName(wishlist.getHotel().getHotelName());
            dto.setHotelImage(wishlist.getHotel().getImage());
            dto.setHotelAddress(wishlist.getHotel().getAddress());
        }

        return dto;
    }
}
