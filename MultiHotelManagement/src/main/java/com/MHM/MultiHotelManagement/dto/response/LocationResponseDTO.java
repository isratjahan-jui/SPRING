package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class LocationResponseDTO {

    private Long id;

    private String locationName;
    private String locationImage;
    private String city;
    private String district;
    private String division;
    private String upazila;

    private Integer totalHotels;
    private LocalDateTime createdAt;

    private List<HotelBasicInfo> hotels;

    @Data
    public static class HotelBasicInfo {
        private Long id;
        private String name;
        private Double pricePerNight;
        private String rating;
        private String status;
    }
}
