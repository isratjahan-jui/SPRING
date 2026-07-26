package com.MHM.MultiHotelManagement.dto.response;

import com.MHM.MultiHotelManagement.enums.ReportType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReportResponseDTO {
    private Long id;
    private int totalBookings;
    private Double income;
    private Double occupancyRate;
    private int totalRooms;
    private ReportType type;
    private String hotelName;
    private LocalDate dateRangeStart;
    private LocalDate dateRangeEnd;
    private LocalDateTime generatedAt;
    private LocalDateTime updatedAt;
}
