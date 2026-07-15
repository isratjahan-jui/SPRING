package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WalletResponseDTO {
    private Long id;
    private Long userId;
    private String userName;
    private BigDecimal balance;
    private BigDecimal totalEarned;
    private BigDecimal totalWithdrawn;
    private LocalDateTime createdAt;
}
