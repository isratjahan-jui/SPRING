package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.HotelRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.HotelResponseDTO;

import java.util.List;

public interface HotelService {
    HotelResponseDTO createHotel(HotelRequestDTO dto);
    HotelResponseDTO getHotelById(Long id);
    List<HotelResponseDTO> getAllApprovedHotels();
    List<HotelResponseDTO> getHotelsByOwner(Long ownerId);
    List<HotelResponseDTO> getHotelsByCity(String city);
    HotelResponseDTO updateHotel(Long id, HotelRequestDTO dto);
    void deleteHotel(Long id);
}
