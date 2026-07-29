package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.HotelDetailsRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.HotelDetailsResponseDTO;

import java.util.Optional;

public interface HotelDetailsService {


    // ✅ Create
    HotelDetailsResponseDTO createHotelDetails(HotelDetailsRequestDTO dto);

    // ✅ Get by Hotel ID
    HotelDetailsResponseDTO getHotelDetailsByHotelId(Long hotelId);

    // ✅ Get by ID
    HotelDetailsResponseDTO getHotelDetailsById(Long id);

    // ✅ Update (Partial Update)
    HotelDetailsResponseDTO updateHotelDetails(Long id, HotelDetailsRequestDTO dto);

    // ✅ Delete
    void deleteHotelDetails(Long id);


}
