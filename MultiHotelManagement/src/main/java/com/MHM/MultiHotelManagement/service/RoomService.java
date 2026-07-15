package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.RoomRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.RoomResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RoomService {

    RoomResponseDTO create(
            RoomRequestDTO dto,
            MultipartFile image
    );

    List<RoomResponseDTO> getAll();

    RoomResponseDTO getById(Long id);

    List<RoomResponseDTO> getByHotelId(Long hotelId);

    List<RoomResponseDTO> getAvailableRooms(Long hotelId);

    RoomResponseDTO update(
            Long id,
            RoomRequestDTO dto,
            MultipartFile image
    );

    void delete(Long id);

    // Booking এর সময় room availability update করে
    void updateAvailability(
            Long roomId,
            int bookedCount
    );
}