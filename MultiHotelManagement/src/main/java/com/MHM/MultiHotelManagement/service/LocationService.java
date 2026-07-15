package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.LocationRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.LocationResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LocationService {

    LocationResponseDTO create(
            LocationRequestDTO dto,
            MultipartFile image
    );

    List<LocationResponseDTO> getAll();

    LocationResponseDTO getById(Long id);

    LocationResponseDTO getByIdWithHotels(Long id);

    List<LocationResponseDTO> getByCity(String city);

    List<LocationResponseDTO> search(String keyword);

    List<LocationResponseDTO> getLocationsWithHotels();

    LocationResponseDTO update(
            Long id,
            LocationRequestDTO dto,
            MultipartFile image
    );

    void delete(Long id);
}