package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.response.ReceiptResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ReceiptService {

    ReceiptResponseDTO generateReceipt(Long paymentId);

    ReceiptResponseDTO getReceiptById(Long id);

    ReceiptResponseDTO getReceiptByNumber(String receiptNumber);

    ReceiptResponseDTO getReceiptByPaymentId(Long paymentId);

    List<ReceiptResponseDTO> getReceiptsByCustomerId(Long customerId);

    List<ReceiptResponseDTO> getReceiptsByHotelId(Long hotelId);

    List<ReceiptResponseDTO> getReceiptsByOwnerId(Long ownerId);

    long countReceiptsByHotelId(Long hotelId);

    BigDecimal sumReceiptsByHotelId(Long hotelId);

    List<ReceiptResponseDTO> getAllReceipts();
}
