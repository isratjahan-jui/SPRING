package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;

@Data
public class WalletRequestDTO {
    private Long userId;
    private Double amount;
    private String type;
    private String description;
    private Long referenceId;
}
