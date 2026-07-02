package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class LocationResponseDTO {

    private Long id;

    // Location info
    private String locationName;
    private String locationImage;
    private String city;

    // এই location এ কতটা hotel আছে
    private Integer totalHotels;

    // Hotel এর basic info (optional)
    private List<HotelBasicInfo> hotels;

    // ── Inner class — Hotel এর basic info ──
    @Data
    public static class HotelBasicInfo {
        private Long id;
        private String name;
        private Double pricePerNight;
        private String rating;
        private String status;
    }
}