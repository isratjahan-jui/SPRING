package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.FacilityRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.FacilityResponseDTO;

import java.util.List;

public interface FacilityService {

    // একটা facility তৈরি করো
    FacilityResponseDTO create(FacilityRequestDTO dto);

    // একসাথে অনেকগুলো facility তৈরি করো
    List<FacilityResponseDTO> createBulk(
            Long hotelId,
            List<FacilityRequestDTO> dtoList
    );

    // সব facility
    List<FacilityResponseDTO> getAll();

    // ID দিয়ে খোঁজো
    FacilityResponseDTO getById(Long id);

    // Hotel ID দিয়ে সব facility খোঁজো
    List<FacilityResponseDTO> getByHotelId(Long hotelId);

    // Update করো
    FacilityResponseDTO update(Long id, FacilityRequestDTO dto);

    // একটা delete করো
    void delete(Long id);

    // Hotel এর সব facility delete করো
    void deleteAllByHotelId(Long hotelId);
}