package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.PaymentRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.PaymentResponseDTO;

import java.util.List;

public interface PaymentService {
    PaymentResponseDTO createPayment(PaymentRequestDTO dto);
    PaymentResponseDTO updatePayment(Long id, PaymentRequestDTO dto);
    PaymentResponseDTO getPaymentById(Long id);
    PaymentResponseDTO getPaymentByBooking(Long bookingId);
    List<PaymentResponseDTO> getAllPayments();
    List<PaymentResponseDTO> getPaymentsByCustomer(Long customerId);
    PaymentResponseDTO processRefund(Long bookingId);
    void deletePayment(Long id);
}
