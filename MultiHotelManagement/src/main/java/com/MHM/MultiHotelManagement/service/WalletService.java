package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.response.WalletResponseDTO;
import com.MHM.MultiHotelManagement.dto.response.WalletTransactionResponseDTO;

import java.util.List;

public interface WalletService {
    WalletResponseDTO getWalletByUserId(Long userId);
    WalletResponseDTO credit(Long userId, Double amount, String description, Long referenceId);
    WalletResponseDTO debit(Long userId, Double amount, String description, Long referenceId);
    List<WalletTransactionResponseDTO> getTransactions(Long walletId);
}
