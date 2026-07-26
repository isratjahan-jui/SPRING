package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.ReportMapper;
import com.MHM.MultiHotelManagement.dto.request.ReportRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.ReportResponseDTO;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.entity.Report;
import com.MHM.MultiHotelManagement.enums.ReportType;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.*;
import com.MHM.MultiHotelManagement.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final RoomRepository roomRepository;

    @Override
    @Transactional
    public ReportResponseDTO generateReport(ReportRequestDTO dto) {
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));

        LocalDate today = LocalDate.now();
        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();

        if (startDate == null || endDate == null) {
            startDate = computeStartDate(today, dto.getType());
            endDate = today;
        }

        Date start = java.sql.Date.valueOf(startDate);
        Date end = java.sql.Date.valueOf(endDate.plusDays(1));

        long totalBookings = bookingRepository.countBookingsByHotelAndDateRange(dto.getHotelId(), start, end);

        BigDecimal income = paymentRepository.sumRevenueByHotelAndDateRange(
                dto.getHotelId(),
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX)
        );

        Integer totalRooms = roomRepository.countTotalRoomsByHotelId(dto.getHotelId());
        if (totalRooms == null || totalRooms == 0) totalRooms = 1;

        int bookedRooms = bookingRepository.countBookedRoomsForHotelInDateRange(dto.getHotelId(), start, end);
        double occupancyRate = Math.min(100.0, (double) bookedRooms / totalRooms * 100.0);

        Report report = new Report();
        report.setTotalBookings((int) totalBookings);
        report.setIncome(income != null ? income.doubleValue() : 0.0);
        report.setOccupancyRate(Math.round(occupancyRate * 10.0) / 10.0);
        report.setTotalRooms(totalRooms);
        report.setType(dto.getType());
        report.setHotel(hotel);
        report.setDateRangeStart(startDate);
        report.setDateRangeEnd(endDate);

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

    private LocalDate computeStartDate(LocalDate today, ReportType type) {
        return switch (type) {
            case DAILY -> today;
            case WEEKLY -> today.minusDays(7);
            case MONTHLY -> today.minusDays(30);
            case YEARLY -> today.minusDays(365);
        };
    }
}
