package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WalletTransactionResponseDTO {
    private Long id;
    private BigDecimal amount;
    private String type;
    private String description;
    private Long referenceId;
    private LocalDateTime createdAt;
}
