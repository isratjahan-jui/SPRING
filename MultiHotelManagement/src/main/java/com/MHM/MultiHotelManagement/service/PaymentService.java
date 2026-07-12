package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.PaymentRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.PaymentResponseDTO;

public interface PaymentService {
    PaymentResponseDTO createPayment(PaymentRequestDTO dto);
    PaymentResponseDTO updatePayment(Long id, PaymentRequestDTO dto);
    PaymentResponseDTO getPaymentById(Long id);
    PaymentResponseDTO getPaymentByBooking(Long bookingId);
    PaymentResponseDTO processRefund(Long bookingId);
    void deletePayment(Long id);
}
