package com.MHM.MultiHotelManagement.service;



import com.MHM.MultiHotelManagement.dto.request.DealsRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.DealsResponseDTO;

import java.util.List;

public interface DealsService {
    DealsResponseDTO createDeal(DealsRequestDTO dto);
    DealsResponseDTO updateDeal(Long id, DealsRequestDTO dto);
    void deleteDeal(Long id);
    List<DealsResponseDTO> getDealsByHotel(Long hotelId);
    List<DealsResponseDTO> getDealsByRoom(Long roomId);
    List<DealsResponseDTO> searchDeals(String keyword);
    List<DealsResponseDTO> getAllActiveDeals();
}
