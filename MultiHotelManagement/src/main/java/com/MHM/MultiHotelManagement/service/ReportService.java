package com.MHM.MultiHotelManagement.service;


import com.MHM.MultiHotelManagement.dto.request.ReportRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.ReportResponseDTO;

import java.util.List;

public interface ReportService {
    ReportResponseDTO generateReport(ReportRequestDTO dto);
    List<ReportResponseDTO> getReportsByHotel(Long hotelId);
    List<ReportResponseDTO> getReportsByType(String type);
}
