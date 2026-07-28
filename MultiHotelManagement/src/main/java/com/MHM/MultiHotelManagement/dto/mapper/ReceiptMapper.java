package com.MHM.MultiHotelManagement.dto.mapper;

import com.MHM.MultiHotelManagement.dto.response.ReceiptResponseDTO;
import com.MHM.MultiHotelManagement.entity.Receipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReceiptMapper {

    private static final Logger log = LoggerFactory.getLogger(ReceiptMapper.class);

    public ReceiptResponseDTO toDTO(Receipt receipt) {
        ReceiptResponseDTO dto = new ReceiptResponseDTO();
        dto.setId(receipt.getId());
        dto.setReceiptNumber(receipt.getReceiptNumber());
        dto.setAmount(receipt.getAmount());
        dto.setTaxAmount(receipt.getTaxAmount());
        dto.setTotalAmount(receipt.getTotalAmount());
        dto.setPaymentMethod(receipt.getPaymentMethod());
        dto.setTransactionId(receipt.getTransactionId());
        dto.setIssuedAt(receipt.getIssuedAt());
        dto.setCreatedAt(receipt.getCreatedAt());

        try {
            if (receipt.getPayment() != null) {
                dto.setPaymentId(receipt.getPayment().getId());
            }
        } catch (Exception e) {
            log.debug("Could not map payment for receipt {}: {}", receipt.getId(), e.getMessage());
        }

        try {
            if (receipt.getInvoice() != null) {
                dto.setInvoiceId(receipt.getInvoice().getId());
                dto.setInvoiceNumber(receipt.getInvoice().getInvoiceNumber());
            }
        } catch (Exception e) {
            log.debug("Could not map invoice for receipt {}: {}", receipt.getId(), e.getMessage());
        }

        try {
            if (receipt.getBooking() != null) {
                dto.setBookingId(receipt.getBooking().getId());
                dto.setBookingReference("BOOK-" + receipt.getBooking().getId());
            }
        } catch (Exception e) {
            log.debug("Could not map booking for receipt {}: {}", receipt.getId(), e.getMessage());
        }

        try {
            if (receipt.getCustomer() != null) {
                dto.setCustomerId(receipt.getCustomer().getId());
                dto.setCustomerName(receipt.getCustomer().getCustomerName());
                if (receipt.getCustomer().getUser() != null) {
                    dto.setCustomerEmail(receipt.getCustomer().getUser().getEmail());
                }
            }
        } catch (Exception e) {
            log.debug("Could not map customer for receipt {}: {}", receipt.getId(), e.getMessage());
        }

        return dto;
    }
}
