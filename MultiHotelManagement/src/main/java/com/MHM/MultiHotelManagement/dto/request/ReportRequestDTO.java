package com.MHM.MultiHotelManagement.dto.request;

import com.MHM.MultiHotelManagement.enums.ReportType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReportRequestDTO {
    private Long hotelId;
    private Long ownerId;
    private ReportType type;
    private LocalDate startDate;
    private LocalDate endDate;
}
