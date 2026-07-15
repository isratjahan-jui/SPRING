package com.MHM.MultiHotelManagement.dto.request;

import com.MHM.MultiHotelManagement.enums.ReportType;
import lombok.Data;

@Data
public class ReportRequestDTO {
    private int totalBookings;
    private Double income;
    private Double occupancyRate;
    private ReportType type;
    private Long hotelId;
}
