package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.WalletRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.WalletResponseDTO;
import com.MHM.MultiHotelManagement.dto.response.WalletTransactionResponseDTO;
import com.MHM.MultiHotelManagement.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @walletService.isOwnWallet(#userId, authentication)")
    public ResponseEntity<WalletResponseDTO> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getWalletByUserId(userId));
    }

    @PostMapping("/credit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WalletResponseDTO> credit(@RequestBody WalletRequestDTO dto) {
        return ResponseEntity.ok(walletService.credit(dto.getUserId(), dto.getAmount(), dto.getDescription(), dto.getReferenceId()));
    }

    @PostMapping("/debit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WalletResponseDTO> debit(@RequestBody WalletRequestDTO dto) {
        return ResponseEntity.ok(walletService.debit(dto.getUserId(), dto.getAmount(), dto.getDescription(), dto.getReferenceId()));
    }

    @GetMapping("/{walletId}/transactions")
    @PreAuthorize("hasRole('ADMIN') or @walletService.isWalletOwner(#walletId, authentication)")
    public ResponseEntity<List<WalletTransactionResponseDTO>> getTransactions(@PathVariable Long walletId) {
        return ResponseEntity.ok(walletService.getTransactions(walletId));
    }
}
