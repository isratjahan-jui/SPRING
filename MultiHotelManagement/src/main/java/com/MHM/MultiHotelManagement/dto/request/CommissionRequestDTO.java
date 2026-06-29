package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;

@Data
public class CommissionRequestDTO {

    private Long bookingId;
    private Double commissionRate;   // যেমন: 10.0 মানে ১০%
}