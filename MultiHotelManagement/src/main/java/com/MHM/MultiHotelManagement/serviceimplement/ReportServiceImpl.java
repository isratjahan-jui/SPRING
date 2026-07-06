package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.ReportMapper;
import com.MHM.MultiHotelManagement.dto.request.ReportRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.ReportResponseDTO;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.entity.Report;
import com.MHM.MultiHotelManagement.enums.ReportType;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.HotelRepository;
import com.MHM.MultiHotelManagement.repository.ReportRepository;
import com.MHM.MultiHotelManagement.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final HotelRepository hotelRepository;

    @Override
    @Transactional
    public ReportResponseDTO generateReport(ReportRequestDTO dto) {
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));

        Report report = new Report();
        report.setTotalBookings(dto.getTotalBookings());
        report.setIncome(dto.getIncome());
        report.setOccupancyRate(dto.getOccupancyRate());
        report.setType(dto.getType());
        report.setHotel(hotel);

        Report saved = reportRepository.save(report);
        return ReportMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportResponseDTO> getReportsByHotel(Long hotelId) {
        return reportRepository.findByHotel_Id(hotelId)
                .stream()
                .map(ReportMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportResponseDTO> getReportsByType(String type) {
        ReportType reportType = ReportType.valueOf(type.toUpperCase());
        return reportRepository.findByType(reportType)
                .stream()
                .map(ReportMapper::toDTO)
                .collect(Collectors.toList());
    }
}
