package com.MHM.MultiHotelManagement.dto.mapper;

import com.MHM.MultiHotelManagement.dto.request.PaymentRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.PaymentResponseDTO;
import com.MHM.MultiHotelManagement.entity.Payment;
import com.MHM.MultiHotelManagement.enums.PaymentStatus;

public class PaymentMapper {

    public static Payment toEntity(PaymentRequestDTO dto) {
        Payment payment = new Payment();
        payment.setMethod(dto.getMethod());
        payment.setAmount(dto.getAmount());
        if (dto.getStatus() != null) {
            payment.setStatus(PaymentStatus.valueOf(dto.getStatus()));
        }
        payment.setCustomerId(dto.getCustomerId());
        return payment;
    }

    public static PaymentResponseDTO toResponseDTO(Payment payment) {
        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setId(payment.getId());
        response.setMethod(payment.getMethod());
        response.setAmount(payment.getAmount());
        if (payment.getStatus() != null) {
            response.setStatus(payment.getStatus().name());
        }
        response.setCustomerId(payment.getCustomerId());
        if (payment.getBooking() != null) {
            response.setBookingId(payment.getBooking().getId());
            response.setBookingReference("BOOK-" + payment.getBooking().getId());
            if (payment.getBooking().getCustomer() != null) {
                response.setCustomerName(payment.getBooking().getCustomer().getUser() != null
                        ? payment.getBooking().getCustomer().getUser().getName()
                        : null);
            }
        }
        if (payment.getExtraService() != null) {
            response.setExtraServiceId(payment.getExtraService().getId());
            response.setServiceType(payment.getExtraService().getServiceType());
        }
        response.setTransactionDate(payment.getTransactionDate());
        return response;
    }
}
