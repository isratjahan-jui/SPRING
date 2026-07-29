package com.MHM.MultiHotelManagement.dto.mapper;

import com.MHM.MultiHotelManagement.dto.request.ExtraServiceRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.ExtraServiceResponseDTO;
import com.MHM.MultiHotelManagement.entity.ExtraService;
import com.MHM.MultiHotelManagement.enums.ServiceStatus;

public class ExtraServiceMapper {

    public static ExtraService toEntity(ExtraServiceRequestDTO dto) {
        ExtraService service = new ExtraService();
        service.setServiceType(dto.getServiceType());
        service.setPrice(dto.getPrice());
        if (dto.getServiceStatus() != null) {
            service.setServiceStatus(ServiceStatus.valueOf(dto.getServiceStatus()));
        }
        return service;
    }

    public static ExtraServiceResponseDTO toResponseDTO(ExtraService service) {
        ExtraServiceResponseDTO response = new ExtraServiceResponseDTO();
        response.setId(service.getId());
        response.setServiceType(service.getServiceType());
        response.setPrice(service.getPrice());
        if (service.getServiceStatus() != null) {
            response.setServiceStatus(service.getServiceStatus().name());
        }
        if (service.getBooking() != null) {
            response.setBookingId(service.getBooking().getId());
            response.setBookingReference("BOOK-" + service.getBooking().getId());
        }
        return response;
    }
}
