package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.ExtraServiceRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.ExtraServiceResponseDTO;

import java.util.List;

public interface ExtraServiceService {


    ExtraServiceResponseDTO createExtraService(ExtraServiceRequestDTO dto);
    ExtraServiceResponseDTO updateExtraService(Long id, ExtraServiceRequestDTO dto);
    ExtraServiceResponseDTO getExtraServiceById(Long id);
    List<ExtraServiceResponseDTO> getExtraServicesByBooking(Long bookingId);
    void deleteExtraService(Long id);
}
