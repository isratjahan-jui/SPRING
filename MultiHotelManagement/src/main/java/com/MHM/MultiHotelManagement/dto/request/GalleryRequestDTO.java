package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;

@Data
public class GalleryRequestDTO {

    private Long hotelId;
    private String caption;

    // INTERIOR, EXTERIOR, FOOD, ROOM, EVENT, OTHER
    private String category;
}