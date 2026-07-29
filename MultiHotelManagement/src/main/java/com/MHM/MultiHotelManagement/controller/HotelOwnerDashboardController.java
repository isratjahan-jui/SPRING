package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.response.OwnerDashboardStatsDTO;
import com.MHM.MultiHotelManagement.service.HotelOwnerDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotel-owner-dashboard")
@RequiredArgsConstructor
public class HotelOwnerDashboardController {

    private final HotelOwnerDashboardService dashboardService;

    @GetMapping("/stats/{ownerId}")
    public ResponseEntity<OwnerDashboardStatsDTO> getStats(@PathVariable Long ownerId) {
        return ResponseEntity.ok(dashboardService.getDashboardStats(ownerId));
    }
}
