package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.CustomerRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.CustomerResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface CustomerService {

    CustomerResponseDTO getCustomerById(Long id);

    CustomerResponseDTO getCustomerByUserId(Long userId);

    Optional<CustomerResponseDTO> getCustomerByEmail(String email);

    List<CustomerResponseDTO> getAllCustomers();

    CustomerResponseDTO saveCustomer(CustomerRequestDTO customerRequestDTO);

    CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO customerRequestDTO, MultipartFile image);

    void deleteCustomer(Long id);
}
