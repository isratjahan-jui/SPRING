package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.request.NotificationRequestDTO;
import com.MHM.MultiHotelManagement.entity.Booking;
import com.MHM.MultiHotelManagement.entity.Commission;
import com.MHM.MultiHotelManagement.entity.Payment;
import com.MHM.MultiHotelManagement.entity.Room;
import com.MHM.MultiHotelManagement.entity.Wallet;
import com.MHM.MultiHotelManagement.entity.WalletTransaction;
import com.MHM.MultiHotelManagement.enums.BookingStatus;
import com.MHM.MultiHotelManagement.enums.NotificationChannel;
import com.MHM.MultiHotelManagement.enums.NotificationType;
import com.MHM.MultiHotelManagement.enums.PaymentStatus;
import com.MHM.MultiHotelManagement.repository.BookingRepository;
import com.MHM.MultiHotelManagement.repository.CommissionRepository;
import com.MHM.MultiHotelManagement.repository.PaymentRepository;
import com.MHM.MultiHotelManagement.repository.RoomRepository;
import com.MHM.MultiHotelManagement.repository.WalletRepository;
import com.MHM.MultiHotelManagement.repository.WalletTransactionRepository;
import com.MHM.MultiHotelManagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingExpiryService {

    private static final Logger log = LoggerFactory.getLogger(BookingExpiryService.class);

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final CommissionRepository commissionRepository;
    private final PaymentRepository paymentRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final NotificationService notificationService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void expireOverduePendingBookings() {
        try {
            Date now = new Date();
            List<Booking> overdueBookings = bookingRepository.findOverduePendingBookings(now);
            for (Booking b : overdueBookings) {
                try {
                    expireOneBooking(b);
                } catch (Exception e) {
                    log.error("Failed to expire booking {}: {}", b.getId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Failed to run overdue booking expiry: {}", e.getMessage());
        }
    }

    private void expireOneBooking(Booking b) {
        b.setStatus(BookingStatus.EXPIRED);

        Room room = b.getRoom();
        if (room != null) {
            room.setAvailableRooms(room.getAvailableRooms() + b.getNumberOfRooms());
            room.setBookedRooms(Math.max(0, room.getBookedRooms() - b.getNumberOfRooms()));
            room.setIsAvailable(true);
            roomRepository.save(room);
        }

        try {
            Commission commission = commissionRepository.findByBooking_Id(b.getId()).orElse(null);
            if (commission != null) {
                commission.setCommissionStatus("CANCELLED");
                commissionRepository.save(commission);
            }
        } catch (Exception ignored) {}

        BigDecimal advancePaid = b.getAdvanceAmount() != null ? b.getAdvanceAmount() : BigDecimal.ZERO;
        if (advancePaid.compareTo(BigDecimal.ZERO) > 0) {
            try {
                Optional<Payment> paymentOpt = paymentRepository.findByBooking_Id(b.getId());
                if (paymentOpt.isPresent() && paymentOpt.get().getStatus() == PaymentStatus.PAID) {
                    paymentOpt.get().setStatus(PaymentStatus.REFUNDED);
                    paymentRepository.save(paymentOpt.get());
                }

                Wallet wallet = walletRepository.findByUser_Id(b.getCustomer().getUser().getId())
                        .orElseGet(() -> {
                            Wallet newWallet = new Wallet();
                            newWallet.setUser(b.getCustomer().getUser());
                            return walletRepository.save(newWallet);
                        });
                wallet.setBalance(wallet.getBalance().add(advancePaid));
                walletRepository.save(wallet);

                WalletTransaction tx = new WalletTransaction();
                tx.setWallet(wallet);
                tx.setAmount(advancePaid);
                tx.setType("CREDIT");
                tx.setDescription("Full refund for expired booking #" + b.getId() + " - Owner did not confirm.");
                tx.setReferenceId(b.getId());
                walletTransactionRepository.save(tx);

                b.setAdvanceAmount(BigDecimal.ZERO);
                b.setCancellationPolicyText("Full refund: Owner did not confirm. ৳" + advancePaid + " refunded to wallet.");
            } catch (Exception e) {
                log.error("Failed to refund expired booking {}: {}", b.getId(), e.getMessage());
            }
        }

        bookingRepository.save(b);

        try {
            String hotelName = b.getHotel().getHotelName();
            Long customerUserId = b.getCustomer().getUser().getId();
            Long ownerUserId = b.getHotel().getOwner().getUser().getId();

            String customerMsg = "Your pending booking #" + b.getId() + " at " + hotelName
                    + " has expired because the hotel owner did not confirm before your check-in date.";
            if (advancePaid.compareTo(BigDecimal.ZERO) > 0) {
                customerMsg += " Your advance of ৳" + advancePaid + " has been refunded to your wallet.";
            }
            sendNotificationToUser(customerUserId, NotificationType.BOOKING_CANCELLED, customerMsg);
            sendNotificationToUser(ownerUserId, NotificationType.BOOKING_CANCELLED,
                    "Booking #" + b.getId() + " at " + hotelName
                            + " has expired. You did not confirm before the check-in date. Room released.");
        } catch (Exception ignored) {}
    }

    private void sendNotificationToUser(Long userId, NotificationType type, String message) {
        NotificationRequestDTO dto = new NotificationRequestDTO();
        dto.setUserId(userId);
        dto.setType(type);
        dto.setChannel(NotificationChannel.WEB);
        dto.setMessage(message);
        notificationService.createNotification(dto);
    }
}
