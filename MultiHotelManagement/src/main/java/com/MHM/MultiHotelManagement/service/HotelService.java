package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.HotelRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.HotelResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HotelService {
    HotelResponseDTO createHotel(HotelRequestDTO dto, MultipartFile image);
    HotelResponseDTO getHotelById(Long id);
    List<HotelResponseDTO> getAllApprovedHotels();
    List<HotelResponseDTO> getHotelsByOwner(Long ownerId);
    List<HotelResponseDTO> getHotelsByCity(String city);
    HotelResponseDTO updateHotel(Long id, HotelRequestDTO dto, MultipartFile image);
    void deleteHotel(Long id);
    List<HotelResponseDTO> getPendingHotels();
    HotelResponseDTO approveHotel(Long id);
    HotelResponseDTO rejectHotel(Long id);
}
