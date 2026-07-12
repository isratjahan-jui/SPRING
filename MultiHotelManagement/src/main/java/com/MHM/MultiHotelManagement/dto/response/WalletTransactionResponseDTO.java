package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WalletTransactionResponseDTO {
    private Long id;
    private Double amount;
    private String type;
    private String description;
    private Long referenceId;
    private LocalDateTime createdAt;
}
