package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.CustomerRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.CustomerResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomerService {

    CustomerResponseDTO create(
            CustomerRequestDTO dto,
            MultipartFile image
    );

    List<CustomerResponseDTO> getAll();

    CustomerResponseDTO getById(Long id);

    CustomerResponseDTO getByUserId(Long userId);

    CustomerResponseDTO update(
            Long id,
            CustomerRequestDTO dto,
            MultipartFile image
    );

    void delete(Long id);
}