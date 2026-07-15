package com.MHM.MultiHotelManagement.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class BookingRequestDTO {
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Hotel ID is required")
    private Long hotelId;

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotNull(message = "Check-in date is required")
    private Date checkInDate;

    @NotNull(message = "Check-out date is required")
    private Date checkOutDate;

    @Min(value = 1, message = "Number of rooms must be at least 1")
    private int numberOfRooms;

    @Min(value = 1, message = "Total guests must be at least 1")
    private int totalGuests;

    @DecimalMin(value = "0.0", message = "Discount rate cannot be negative")
    private BigDecimal discountRate;

    @DecimalMin(value = "0.0", message = "Advance amount cannot be negative")
    private BigDecimal advanceAmount;

    private List<Long> foodItemIds;
}
