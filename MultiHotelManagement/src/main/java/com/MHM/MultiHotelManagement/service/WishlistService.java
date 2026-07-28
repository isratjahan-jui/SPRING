package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.WishlistRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.WishlistResponseDTO;

import java.util.List;

public interface WishlistService {

    WishlistResponseDTO addToWishlist(WishlistRequestDTO dto);

    void removeFromWishlist(Long id);

    List<WishlistResponseDTO> getAll();

    List<WishlistResponseDTO> getByUserId(Long userId);

    List<WishlistResponseDTO> getByCustomerId(Long customerId);

    List<WishlistResponseDTO> getByHotelId(Long hotelId);

    boolean existsByCustomerAndHotel(Long customerId, Long hotelId);

    boolean existsByUserAndHotel(Long userId, Long hotelId);
}
