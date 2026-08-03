package com.MHM.MultiHotelManagement.dto.mapper;

import com.MHM.MultiHotelManagement.dto.response.WalletResponseDTO;
import com.MHM.MultiHotelManagement.dto.response.WalletTransactionResponseDTO;
import com.MHM.MultiHotelManagement.entity.Wallet;
import com.MHM.MultiHotelManagement.entity.WalletTransaction;

public class WalletMapper {

    public static WalletResponseDTO toDTO(Wallet wallet) {
        WalletResponseDTO dto = new WalletResponseDTO();
        dto.setId(wallet.getId());
        dto.setUserId(wallet.getUser().getId());
        dto.setUserName(wallet.getUser().getName());
        dto.setBalance(wallet.getBalance());
        dto.setTotalEarned(wallet.getTotalEarned());
        dto.setTotalWithdrawn(wallet.getTotalWithdrawn());
        dto.setCreatedAt(wallet.getCreatedAt());
        return dto;
    }

    public static WalletTransactionResponseDTO toTransactionDTO(WalletTransaction tx) {
        WalletTransactionResponseDTO dto = new WalletTransactionResponseDTO();
        dto.setId(tx.getId());
        dto.setAmount(tx.getAmount());
        dto.setType(tx.getType());
        dto.setDescription(tx.getDescription());
        dto.setReferenceId(tx.getReferenceId());
        dto.setCreatedAt(tx.getCreatedAt());
        return dto;
    }
}
