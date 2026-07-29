package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;

@Data
public class SupportStatsResponseDTO {
    private long totalTickets;
    private long pendingCount;
    private long inProgressCount;
    private long escalatedCount;
    private long resolvedCount;
    private long closedCount;
    private long unassignedCount;
    private double avgResponseTimeHours;
    private double avgResolutionTimeHours;
}
