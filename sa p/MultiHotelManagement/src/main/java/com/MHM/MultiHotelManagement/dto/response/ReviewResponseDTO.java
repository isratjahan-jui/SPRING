package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewResponseDTO {
    private Long id;
    private int rating;
    private String comment;
    private Long customerId;
    private Long hotelId;
    private Long bookingId;
    private String customerName;
    private String hotelName;
    private String hotelAddress;
    private String roomType;
    private String bookingStatus;
    private String status;
    private String ownerReply;
    private LocalDateTime replyAt;
    private int editCount;
    private LocalDateTime editedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
