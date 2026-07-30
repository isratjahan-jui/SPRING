package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;

@Data
public class OwnerDashboardStatsDTO {
    private Long totalHotels;
    private Long totalRooms;
    private Long totalBookings;
    private Long pendingBookings;
    private Long confirmedBookings;
    private Long checkedInBookings;
    private Long checkedOutBookings;
    private Long cancelledBookings;
    private Long noShowBookings;
    private Double totalRevenue;
    private Double totalDue;
}
