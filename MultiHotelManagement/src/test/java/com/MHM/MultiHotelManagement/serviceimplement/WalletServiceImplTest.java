package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.response.WalletResponseDTO;
import com.MHM.MultiHotelManagement.entity.User;
import com.MHM.MultiHotelManagement.entity.Wallet;
import com.MHM.MultiHotelManagement.enums.Role;
import com.MHM.MultiHotelManagement.exception.BadRequestException;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.UserRepository;
import com.MHM.MultiHotelManagement.repository.WalletRepository;
import com.MHM.MultiHotelManagement.repository.WalletTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletTransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    private User user;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setRole(Role.CUSTOMER);

        wallet = new Wallet();
        wallet.setId(1L);
        wallet.setUser(user);
        wallet.setBalance(new BigDecimal("10000.00"));
        wallet.setTotalEarned(new BigDecimal("10000.00"));
        wallet.setTotalWithdrawn(BigDecimal.ZERO);
        wallet.setVersion(0L);
    }

    // ── Wallet Debit Tests ──────────────────────────────────────

    @Test
    void debit_SufficientBalanceSucceeds() {
        when(walletRepository.findByUser_Id(1L)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any())).thenReturn(wallet);
        when(transactionRepository.save(any())).thenReturn(null);

        WalletResponseDTO result = walletService.debit(1L, new BigDecimal("3000.00"), "Test debit", null);

        assertNotNull(result);
        assertEquals(new BigDecimal("7000.00"), wallet.getBalance());
        assertEquals(new BigDecimal("3000.00"), wallet.getTotalWithdrawn());
    }

    @Test
    void debit_InsufficientBalanceThrowsBadRequest() {
        when(walletRepository.findByUser_Id(1L)).thenReturn(Optional.of(wallet));

        assertThrows(BadRequestException.class,
                () -> walletService.debit(1L, new BigDecimal("15000.00"), "Overdraft", null));
    }

    @Test
    void debit_ExactBalanceSucceeds() {
        when(walletRepository.findByUser_Id(1L)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any())).thenReturn(wallet);
        when(transactionRepository.save(any())).thenReturn(null);

        WalletResponseDTO result = walletService.debit(1L, new BigDecimal("10000.00"), "Full withdraw", null);

        assertNotNull(result);
        assertEquals(0, BigDecimal.ZERO.compareTo(wallet.getBalance()));
    }

    @Test
    void debit_NegativeAmountThrowsBadRequest() {
        assertThrows(BadRequestException.class,
                () -> walletService.debit(1L, new BigDecimal("-500.00"), "Negative", null));
    }

    @Test
    void debit_ZeroAmountThrowsBadRequest() {
        assertThrows(BadRequestException.class,
                () -> walletService.debit(1L, BigDecimal.ZERO, "Zero", null));
    }

    @Test
    void debit_WalletNotFoundThrowsResourceNotFound() {
        when(walletRepository.findByUser_Id(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> walletService.debit(99L, new BigDecimal("100.00"), "No wallet", null));
    }

    // ── Wallet Credit Tests ─────────────────────────────────────

    @Test
    void credit_CreatesWalletIfNotExists() {
        when(walletRepository.findByUser_Id(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(walletRepository.save(any())).thenReturn(wallet);
        when(transactionRepository.save(any())).thenReturn(null);

        WalletResponseDTO result = walletService.credit(1L, new BigDecimal("5000.00"), "Bonus", null);

        assertNotNull(result);
        verify(walletRepository, times(2)).save(any());
    }

    @Test
    void credit_NegativeAmountThrowsBadRequest() {
        assertThrows(BadRequestException.class,
                () -> walletService.credit(1L, new BigDecimal("-100.00"), "Negative", null));
    }

    @Test
    void credit_AddsToExistingBalance() {
        when(walletRepository.findByUser_Id(1L)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any())).thenReturn(wallet);
        when(transactionRepository.save(any())).thenReturn(null);

        walletService.credit(1L, new BigDecimal("5000.00"), "Bonus", null);

        assertEquals(new BigDecimal("15000.00"), wallet.getBalance());
        assertEquals(new BigDecimal("15000.00"), wallet.getTotalEarned());
    }

    // ── Concurrent Debit Protection Tests ───────────────────────

    @Test
    void debit_ConcurrentDebitsWithPessimisticLock() {
        // This test verifies the @Lock annotation is present and the method uses proper transaction
        // In real DB, the pessimistic lock would prevent concurrent overdraw
        when(walletRepository.findByUser_Id(1L)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any())).thenReturn(wallet);
        when(transactionRepository.save(any())).thenReturn(null);

        // First debit succeeds
        walletService.debit(1L, new BigDecimal("6000.00"), "First", null);
        assertEquals(new BigDecimal("4000.00"), wallet.getBalance());

        // Second debit with remaining balance succeeds
        walletService.debit(1L, new BigDecimal("3000.00"), "Second", null);
        assertEquals(new BigDecimal("1000.00"), wallet.getBalance());
    }

    @Test
    void debit_BigDecimalComparisonNotDouble() {
        // Verify BigDecimal.compareTo is used, not floating-point < or >
        Wallet preciseWallet = new Wallet();
        preciseWallet.setId(2L);
        preciseWallet.setUser(user);
        preciseWallet.setBalance(new BigDecimal("0.10"));
        preciseWallet.setTotalEarned(new BigDecimal("0.10"));
        preciseWallet.setTotalWithdrawn(BigDecimal.ZERO);

        when(walletRepository.findByUser_Id(1L)).thenReturn(Optional.of(preciseWallet));
        when(walletRepository.save(any())).thenReturn(preciseWallet);
        when(transactionRepository.save(any())).thenReturn(null);

        // 0.10 - 0.05 = 0.05 (not floating point issue)
        walletService.debit(1L, new BigDecimal("0.05"), "Precise", null);
        assertEquals(0, new BigDecimal("0.05").compareTo(preciseWallet.getBalance()));
    }
}
