package com.MHM.MultiHotelManagement.dto.mapper;

import com.MHM.MultiHotelManagement.dto.response.InvoiceResponseDTO;
import com.MHM.MultiHotelManagement.entity.Invoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper {

    private static final Logger log = LoggerFactory.getLogger(InvoiceMapper.class);

    public InvoiceResponseDTO toDTO(Invoice invoice) {
        InvoiceResponseDTO dto = new InvoiceResponseDTO();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setTaxAmount(invoice.getTaxAmount());
        dto.setDiscountAmount(invoice.getDiscountAmount());
        dto.setNetAmount(invoice.getNetAmount());
        dto.setStatus(invoice.getStatus());

        try {
            if (invoice.getBooking() != null) {
                dto.setBookingId(invoice.getBooking().getId());
                dto.setBookingStatus(invoice.getBooking().getStatus() != null
                        ? invoice.getBooking().getStatus().name() : null);
                if (invoice.getBooking().getHotel() != null) {
                    dto.setHotelName(invoice.getBooking().getHotel().getHotelName());
                }
                if (invoice.getBooking().getRoom() != null) {
                    dto.setRoomType(invoice.getBooking().getRoom().getRoomType());
                }
            }
        } catch (Exception e) {
            log.debug("Could not map booking details for invoice {}: {}", invoice.getId(), e.getMessage());
        }

        try {
            if (invoice.getPayment() != null) {
                dto.setPaymentId(invoice.getPayment().getId());
            }
        } catch (Exception e) {
            log.debug("Could not map payment for invoice {}: {}", invoice.getId(), e.getMessage());
        }

        try {
            if (invoice.getCustomer() != null) {
                dto.setCustomerId(invoice.getCustomer().getId());
                dto.setCustomerName(invoice.getCustomer().getCustomerName());
            }
        } catch (Exception e) {
            log.debug("Could not map customer for invoice {}: {}", invoice.getId(), e.getMessage());
        }

        try {
            if (invoice.getCommission() != null) {
                dto.setCommissionId(invoice.getCommission().getId());
            }
        } catch (Exception e) {
            log.debug("Could not map commission for invoice {}: {}", invoice.getId(), e.getMessage());
        }

        dto.setIssuedAt(invoice.getIssuedAt());
        dto.setCreatedAt(invoice.getCreatedAt());
        dto.setUpdatedAt(invoice.getUpdatedAt());
        return dto;
    }
}
