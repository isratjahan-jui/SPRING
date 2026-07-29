package com.MHM.MultiHotelManagement.service;


import com.MHM.MultiHotelManagement.dto.request.ReportRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.ReportResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReportService {
    ReportResponseDTO generateReport(ReportRequestDTO dto);
    List<ReportResponseDTO> getReportsByHotel(Long hotelId);
    List<ReportResponseDTO> getReportsByType(String type);
    Map<String, Object> getPlatformSummary(LocalDate startDate, LocalDate endDate);
}
