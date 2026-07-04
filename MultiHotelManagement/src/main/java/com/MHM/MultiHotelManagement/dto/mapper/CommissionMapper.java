package com.MHM.MultiHotelManagement.dto.mapper;

import com.MHM.MultiHotelManagement.dto.response.CommissionResponseDTO;
import com.MHM.MultiHotelManagement.entity.Commission;

public class CommissionMapper {

    // Entity → ResponseDTO
    public static CommissionResponseDTO toDTO( Commission commission ) {
        CommissionResponseDTO dto = new CommissionResponseDTO();

        dto.setId(commission.getId());
        dto.setCommissionRate(commission.getCommissionRate());
        dto.setAdminEarnings(commission.getAdminEarnings());
        dto.setHotelOwnerEarnings(commission.getHotelOwnerEarnings());
        dto.setCreatedAt(commission.getCreatedAt());
        dto.setUpdatedAt(commission.getUpdatedAt());

        // Booking info
        if (commission.getBooking() != null) {
            dto.setBookingId(commission.getBooking().getId());
            dto.setBookingReference("BOOK-" + commission.getBooking().getId());
            dto.setBookingTotalPrice(commission.getBooking().getTotalPrice() );
            dto.setBookingStatus( commission.getBooking().getStatus() != null
                            ? commission.getBooking().getStatus().name() : null );

            // Hotel info
            if (commission.getBooking().getHotel() != null) {
                dto.setHotelId( commission.getBooking().getHotel().getId());
                dto.setHotelName(commission.getBooking().getHotel().getHotelName());

                // Owner info
                if (commission.getBooking().getHotel().getOwner() != null) {
                    dto.setOwnerId(commission.getBooking() .getHotel().getOwner().getId());

                    if (commission.getBooking().getHotel() .getOwner().getUser() != null) {
                        dto.setOwnerName(commission.getBooking()
                                .getHotel().getOwner()
                                .getUser().getName());
                    }
                }
            }

            // Customer info
            if (commission.getBooking().getCustomer() != null
                    && commission.getBooking()
                    .getCustomer().getUser() != null) {
                dto.setCustomerName(commission.getBooking()
                        .getCustomer().getUser().getName());
            }
        }

        // Payment info
        if (commission.getPayment() != null) {
            dto.setPaymentId(commission.getPayment().getId());
            dto.setPaymentMethod(commission.getPayment().getMethod());
            dto.setPaymentStatus(
                    commission.getPayment().getStatus() != null
                            ? commission.getPayment().getStatus().name()
                            : null
            );
        }

        // ExtraService info
        if (commission.getExtraService() != null) {
            dto.setExtraServiceId(commission.getExtraService().getId());
            dto.setServiceType(commission.getExtraService().getServiceType());
            dto.setExtraServicePrice(commission.getExtraService().getPrice());
        }

        // Audit info (যদি entity তে থাকে)
        // dto.setCreatedBy(commission.getCreatedBy());
        // dto.setUpdatedBy(commission.getUpdatedBy());





        return dto;
    }


}