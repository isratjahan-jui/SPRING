package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.WalletMapper;
import com.MHM.MultiHotelManagement.dto.response.WalletResponseDTO;
import com.MHM.MultiHotelManagement.dto.response.WalletTransactionResponseDTO;
import com.MHM.MultiHotelManagement.entity.User;
import com.MHM.MultiHotelManagement.entity.Wallet;
import com.MHM.MultiHotelManagement.entity.WalletTransaction;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.UserRepository;
import com.MHM.MultiHotelManagement.repository.WalletRepository;
import com.MHM.MultiHotelManagement.repository.WalletTransactionRepository;
import com.MHM.MultiHotelManagement.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public WalletResponseDTO credit(Long userId, Double amount, String description, Long referenceId) {
        Wallet wallet = walletRepository.findByUser_Id(userId)
                .orElseGet(() -> createWalletForUser(userId));

        wallet.setBalance(wallet.getBalance() + amount);
        wallet.setTotalEarned(wallet.getTotalEarned() + amount);
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
    @Transactional
    public WalletResponseDTO debit(Long userId, Double amount, String description, Long referenceId) {
        Wallet wallet = walletRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));

        if (wallet.getBalance() < amount) {
            throw new IllegalStateException("Insufficient wallet balance");
        }

        wallet.setBalance(wallet.getBalance() - amount);
        wallet.setTotalWithdrawn(wallet.getTotalWithdrawn() + amount);
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

    private Wallet createWalletForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(0.0);
        wallet.setTotalEarned(0.0);
        wallet.setTotalWithdrawn(0.0);
        return walletRepository.save(wallet);
    }
}
