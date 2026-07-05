package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.InvoiceRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.InvoiceResponseDTO;

import java.util.List;

public interface InvoiceService {
    InvoiceResponseDTO create(InvoiceRequestDTO dto);
    InvoiceResponseDTO getById(Long id);
    List<InvoiceResponseDTO> getByCustomer(Long customerId);
    List<InvoiceResponseDTO> getAll();
    void delete(Long id);
}
