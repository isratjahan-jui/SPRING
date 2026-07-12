package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WalletResponseDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Double balance;
    private Double totalEarned;
    private Double totalWithdrawn;
    private LocalDateTime createdAt;
}
