package com.MHM.MultiHotelManagement.dto.mapper;


import com.MHM.MultiHotelManagement.dto.request.InvoiceRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.InvoiceResponseDTO;
import com.MHM.MultiHotelManagement.entity.Invoice;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper {

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
            dto.setBookingId(invoice.getBooking() != null ? invoice.getBooking().getId() : null);
            dto.setPaymentId(invoice.getPayment() != null ? invoice.getPayment().getId() : null);
            dto.setCustomerId(invoice.getCustomer() != null ? invoice.getCustomer().getId() : null);
            dto.setCommissionId(invoice.getCommission() != null ? invoice.getCommission().getId() : null);
        } catch (Exception ignored) {
        }
        dto.setIssuedAt(invoice.getIssuedAt());
        dto.setCreatedAt(invoice.getCreatedAt());
        dto.setUpdatedAt(invoice.getUpdatedAt());
        return dto;
    }
}
