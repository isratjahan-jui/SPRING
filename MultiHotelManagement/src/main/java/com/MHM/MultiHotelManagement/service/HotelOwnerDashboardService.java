package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.response.OwnerDashboardStatsDTO;

public interface HotelOwnerDashboardService {
    OwnerDashboardStatsDTO getDashboardStats(Long ownerId);
}
