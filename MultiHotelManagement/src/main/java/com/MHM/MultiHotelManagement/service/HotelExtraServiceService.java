package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.HotelExtraServiceRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.HotelExtraServiceResponseDTO;

import java.util.List;

public interface HotelExtraServiceService {
    HotelExtraServiceResponseDTO create(HotelExtraServiceRequestDTO dto);
    HotelExtraServiceResponseDTO update(Long id, HotelExtraServiceRequestDTO dto);
    HotelExtraServiceResponseDTO getById(Long id);
    List<HotelExtraServiceResponseDTO> getByHotel(Long hotelId);
    List<HotelExtraServiceResponseDTO> getActiveByHotel(Long hotelId);
    void delete(Long id);
}
