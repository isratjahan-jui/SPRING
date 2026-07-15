package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.response.WalletResponseDTO;
import com.MHM.MultiHotelManagement.dto.response.WalletTransactionResponseDTO;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {
    WalletResponseDTO getWalletByUserId(Long userId);
    WalletResponseDTO credit(Long userId, BigDecimal amount, String description, Long referenceId);
    WalletResponseDTO debit(Long userId, BigDecimal amount, String description, Long referenceId);
    List<WalletTransactionResponseDTO> getTransactions(Long walletId);
    boolean isOwnWallet(Long userId, Authentication authentication);
    boolean isWalletOwner(Long walletId, Authentication authentication);
}
