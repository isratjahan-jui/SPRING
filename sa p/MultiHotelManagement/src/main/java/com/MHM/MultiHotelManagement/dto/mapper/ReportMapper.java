package com.MHM.MultiHotelManagement.dto.mapper;

import com.MHM.MultiHotelManagement.dto.response.ReportResponseDTO;
import com.MHM.MultiHotelManagement.entity.Report;

public class ReportMapper {
    public static ReportResponseDTO toDTO(Report report) {
        ReportResponseDTO dto = new ReportResponseDTO();
        dto.setId(report.getId());
        dto.setTotalBookings(report.getTotalBookings());
        dto.setIncome(report.getIncome());
        dto.setOccupancyRate(report.getOccupancyRate());
        dto.setTotalRooms(report.getTotalRooms());
        dto.setType(report.getType());
        dto.setHotelName(report.getHotel().getHotelName());
        dto.setDateRangeStart(report.getDateRangeStart());
        dto.setDateRangeEnd(report.getDateRangeEnd());
        dto.setGeneratedAt(report.getGeneratedAt());
        dto.setUpdatedAt(report.getUpdatedAt());
        return dto;
    }
}
