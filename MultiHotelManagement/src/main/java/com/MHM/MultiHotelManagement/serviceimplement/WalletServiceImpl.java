package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.WalletMapper;
import com.MHM.MultiHotelManagement.dto.response.WalletResponseDTO;
import com.MHM.MultiHotelManagement.dto.response.WalletTransactionResponseDTO;
import com.MHM.MultiHotelManagement.entity.User;
import com.MHM.MultiHotelManagement.entity.Wallet;
import com.MHM.MultiHotelManagement.entity.WalletTransaction;
import com.MHM.MultiHotelManagement.exception.BadRequestException;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.UserRepository;
import com.MHM.MultiHotelManagement.repository.WalletRepository;
import com.MHM.MultiHotelManagement.repository.WalletTransactionRepository;
import com.MHM.MultiHotelManagement.service.WalletService;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public WalletResponseDTO getWalletByUserId(Long userId) {
        Wallet wallet = walletRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));
        return WalletMapper.toDTO(wallet);
    }

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    public WalletResponseDTO credit(Long userId, BigDecimal amount, String description, Long referenceId) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Credit amount must be positive");
        }

        Wallet wallet = walletRepository.findByUser_Id(userId)
                .orElseGet(() -> createWalletForUser(userId));

        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setTotalEarned(wallet.getTotalEarned().add(amount));
        Wallet saved = walletRepository.save(wallet);

        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(saved);
        tx.setAmount(amount);
        tx.setType("CREDIT");
        tx.setDescription(description);
        tx.setReferenceId(referenceId);
        transactionRepository.save(tx);

        return WalletMapper.toDTO(saved);
    }

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    public WalletResponseDTO debit(Long userId, BigDecimal amount, String description, Long referenceId) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Debit amount must be positive");
        }

        Wallet wallet = walletRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException("Insufficient wallet balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setTotalWithdrawn(wallet.getTotalWithdrawn().add(amount));
        Wallet saved = walletRepository.save(wallet);

        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(saved);
        tx.setAmount(amount);
        tx.setType("DEBIT");
        tx.setDescription(description);
        tx.setReferenceId(referenceId);
        transactionRepository.save(tx);

        return WalletMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletTransactionResponseDTO> getTransactions(Long walletId) {
        return transactionRepository.findByWallet_IdOrderByCreatedAtDesc(walletId)
                .stream().map(WalletMapper::toTransactionDTO).collect(Collectors.toList());
    }

    @Override
    public boolean isOwnWallet(Long userId, Authentication authentication) {
        if (authentication == null) return false;
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .map(user -> user.getId().equals(userId))
                .orElse(false);
    }

    @Override
    public boolean isWalletOwner(Long walletId, Authentication authentication) {
        if (authentication == null) return false;
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .flatMap(user -> walletRepository.findByUser_Id(user.getId()))
                .map(wallet -> wallet.getId().equals(walletId))
                .orElse(false);
    }

    private Wallet createWalletForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setTotalEarned(BigDecimal.ZERO);
        wallet.setTotalWithdrawn(BigDecimal.ZERO);
        return walletRepository.save(wallet);
    }
}
