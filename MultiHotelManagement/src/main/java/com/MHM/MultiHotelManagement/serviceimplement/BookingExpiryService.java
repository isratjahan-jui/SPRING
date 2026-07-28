package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.request.NotificationRequestDTO;
import com.MHM.MultiHotelManagement.entity.Booking;
import com.MHM.MultiHotelManagement.entity.Commission;
import com.MHM.MultiHotelManagement.entity.HotelDetails;
import com.MHM.MultiHotelManagement.entity.Payment;
import com.MHM.MultiHotelManagement.entity.Room;
import com.MHM.MultiHotelManagement.enums.BookingStatus;
import com.MHM.MultiHotelManagement.enums.NotificationChannel;
import com.MHM.MultiHotelManagement.enums.NotificationType;
import com.MHM.MultiHotelManagement.enums.PaymentStatus;
import com.MHM.MultiHotelManagement.repository.BookingRepository;
import com.MHM.MultiHotelManagement.repository.CommissionRepository;
import com.MHM.MultiHotelManagement.repository.HotelDetailsRepository;
import com.MHM.MultiHotelManagement.repository.PaymentRepository;
import com.MHM.MultiHotelManagement.repository.RoomRepository;
import com.MHM.MultiHotelManagement.service.NotificationService;
import com.MHM.MultiHotelManagement.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    private final WalletService walletService;

    private final HotelDetailsRepository hotelDetailsRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 */1 * * *")
    @Transactional
    public void processExpiredAndNoShow() {
        Date now = new Date();
        int noShowCount = 0;
        int expiredCount = 0;

        List<Booking> confirmedBookings = bookingRepository.findByStatus(BookingStatus.CONFIRMED);
        for (Booking b : confirmedBookings) {
            try {
                if (b.getCheckOutDate() != null && b.getCheckOutDate().before(now)) {
                    processNoShow(b);
                    noShowCount++;
                }
            } catch (Exception e) {
                log.error("Failed to process NO_SHOW for booking {}: {}", b.getId(), e.getMessage());
            }
        }

        List<Booking> overduePending = bookingRepository.findOverduePendingBookings(now);
        for (Booking b : overduePending) {
            try {
                processExpired(b);
                expiredCount++;
            } catch (Exception e) {
                log.error("Failed to process EXPIRED for booking {}: {}", b.getId(), e.getMessage());
            }
        }

        if (noShowCount > 0 || expiredCount > 0) {
            log.info("BookingExpiryService: {} bookings marked NO_SHOW, {} bookings marked EXPIRED",
                    noShowCount, expiredCount);
        }
    }

    private void processExpired(Booking b) {
        b.setStatus(BookingStatus.EXPIRED);

        restoreRoom(b);
        cancelCommission(b);
        refundAdvanceOnExpiry(b);
        bookingRepository.save(b);
        sendExpiredNotifications(b);
    }

    private void processNoShow(Booking b) {
        b.setStatus(BookingStatus.NO_SHOW);

        restoreRoom(b);
        cancelCommission(b);
        refundOnNoShow(b);
        bookingRepository.save(b);
        sendNoShowNotifications(b);
    }

    private void restoreRoom(Booking b) {
        try {
            Room room = b.getRoom();
            if (room != null) {
                room.setAvailableRooms(room.getAvailableRooms() + b.getNumberOfRooms());
                room.setBookedRooms(Math.max(0, room.getBookedRooms() - b.getNumberOfRooms()));
                room.setIsAvailable(true);
                roomRepository.save(room);
            }
        } catch (Exception e) {
            log.error("Failed to restore room for booking {}: {}", b.getId(), e.getMessage());
        }
    }

    private void cancelCommission(Booking b) {
        try {
            Commission commission = commissionRepository.findByBooking_Id(b.getId()).orElse(null);
            if (commission != null) {
                commission.setCommissionStatus("CANCELLED");
                commissionRepository.save(commission);
            }
        } catch (Exception e) {
            log.debug("No commission to cancel for booking {}", b.getId());
        }
    }

    private void refundAdvanceOnExpiry(Booking b) {
        try {
            BigDecimal advancePaid = b.getAdvanceAmount() != null ? b.getAdvanceAmount() : BigDecimal.ZERO;
            if (advancePaid.compareTo(BigDecimal.ZERO) <= 0) {
                return;
            }

            Optional<Payment> paymentOpt = paymentRepository.findByBooking_Id(b.getId());
            if (paymentOpt.isPresent()) {
                Payment payment = paymentOpt.get();
                if (payment.getStatus() == PaymentStatus.PAID) {
                    payment.setStatus(PaymentStatus.REFUNDED);
                    paymentRepository.save(payment);
                }
            }

            Long customerUserId = b.getCustomer().getUser().getId();

            Wallet wallet = walletRepository.findByUser_Id(customerUserId)
                    .orElseGet(() -> {
                        Wallet newWallet = new Wallet();
                        newWallet.setUser(b.getCustomer().getUser());
                        return walletRepository.save(newWallet);
                    });

            wallet.setBalance(wallet.getBalance().add(advancePaid));
            walletRepository.save(wallet);

            WalletTransaction transaction = new WalletTransaction();
            transaction.setWallet(wallet);
            transaction.setAmount(advancePaid);
            transaction.setType("CREDIT");
            transaction.setDescription("Full refund for expired booking #" + b.getId()
                    + " - Owner did not confirm. ৳" + advancePaid + " credited to wallet.");
            transaction.setReferenceId(b.getId());
            walletTransactionRepository.save(transaction);

            walletService.credit(
                    customerUserId,
                    advancePaid,
                    "Full refund for expired booking #" + b.getId()
                            + " - Owner did not confirm. ৳" + advancePaid + " credited to wallet.",
                    b.getId()
            );


            b.setAdvanceAmount(BigDecimal.ZERO);
            b.setCancellationPolicyText("Full refund: Owner did not confirm. Advance ৳" + advancePaid + " refunded to wallet.");

            log.info("Refunded ৳{} for expired booking {}", advancePaid, b.getId());
        } catch (Exception e) {
            log.error("Failed to refund advance for expired booking {}: {}", b.getId(), e.getMessage());
        }
    }

    private void refundOnNoShow(Booking b) {
        try {
            BigDecimal advancePaid = b.getAdvanceAmount() != null ? b.getAdvanceAmount() : BigDecimal.ZERO;
            if (advancePaid.compareTo(BigDecimal.ZERO) <= 0) {
                return;
            }

            HotelDetails hotelDetails = hotelDetailsRepository.findByHotel_Id(b.getHotel().getId()).orElse(null);
            String policy = hotelDetails != null ? hotelDetails.getCancellationDepositRefundable() : "NON_REFUNDABLE";

            BigDecimal refundAmount = BigDecimal.ZERO;
            String refundNote = "";

            switch (policy) {
                case "FULL_REFUND":
                    refundAmount = advancePaid;
                    refundNote = "No-show refund: Full refund per hotel policy.";
                    break;
                case "PARTIAL_REFUND":
                    refundAmount = advancePaid.multiply(BigDecimal.valueOf(0.5)).setScale(2, RoundingMode.HALF_UP);
                    refundNote = "No-show refund: 50% per hotel policy.";
                    break;
                case "CONDITIONAL_REFUND":
                    refundAmount = advancePaid.multiply(BigDecimal.valueOf(0.3)).setScale(2, RoundingMode.HALF_UP);
                    refundNote = "No-show refund: 30% per hotel policy (late/no-show).";
                    break;
                case "NON_REFUNDABLE":
                default:
                    refundAmount = BigDecimal.ZERO;
                    refundNote = "No-show: Non-refundable per hotel policy. No refund issued.";
                    break;
            }

            if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
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
                wallet.setBalance(wallet.getBalance().add(refundAmount));
                walletRepository.save(wallet);

                WalletTransaction tx = new WalletTransaction();
                tx.setWallet(wallet);
                tx.setAmount(refundAmount);
                tx.setType("CREDIT");
                tx.setDescription(refundNote + " Booking #" + b.getId() + ". ৳" + refundAmount + " credited.");
                tx.setReferenceId(b.getId());
                walletTransactionRepository.save(tx);

                walletService.credit(
                        b.getCustomer().getUser().getId(),
                        refundAmount,
                        refundNote + " Booking #" + b.getId() + ". ৳" + refundAmount + " credited.",
                        b.getId()
                );

            }

            b.setAdvanceAmount(advancePaid.subtract(refundAmount));
            b.setCancellationPolicyText(refundNote);
            log.info("No-show refund for booking {}: {} (policy: {}, refund: ৳{})", b.getId(), refundNote, policy, refundAmount);
        } catch (Exception e) {
            log.error("Failed to process no-show refund for booking {}: {}", b.getId(), e.getMessage());
        }
    }

    private void sendExpiredNotifications(Booking b) {
        try {
            String hotelName = b.getHotel().getHotelName();
            Long customerUserId = b.getCustomer().getUser().getId();
            Long ownerUserId = b.getHotel().getOwner().getUser().getId();

            BigDecimal advancePaid = b.getAdvanceAmount() != null ? b.getAdvanceAmount() : BigDecimal.ZERO;

            String customerMsg = "Your pending booking #" + b.getId() + " at " + hotelName
                    + " has expired (owner did not confirm before check-out date). "
                    + "The room has been released.";
            if (advancePaid.compareTo(BigDecimal.ZERO) > 0) {
                customerMsg += " Your full advance of ৳" + advancePaid
                        + " has been refunded to your wallet. You can rebook for new dates.";
            } else {
                customerMsg += " Please rebook if you need accommodation.";
            }
            sendNotificationToUser(customerUserId, NotificationType.BOOKING_CANCELLED, customerMsg);

            sendNotificationToUser(ownerUserId, NotificationType.BOOKING_CANCELLED,
                    "Booking #" + b.getId() + " at " + hotelName
                            + " has expired because it was not confirmed before the check-out date. "
                            + "The room has been released. Customer has been notified and refunded if applicable.");
        } catch (Exception e) {
            log.debug("Failed to send expired notifications for booking {}", b.getId());
        }
    }

    private void sendNoShowNotifications(Booking b) {
        try {
            String hotelName = b.getHotel().getHotelName();
            Long customerUserId = b.getCustomer().getUser().getId();
            Long ownerUserId = b.getHotel().getOwner().getUser().getId();

            sendNotificationToUser(customerUserId, NotificationType.BOOKING_CANCELLED,
                    "Your booking #" + b.getId() + " at " + hotelName
                            + " has been marked as No-Show (check-in not completed by "
                            + b.getCheckInDate() + "). The room has been released. "
                            + "Please rebook if you still need accommodation.");

            sendNotificationToUser(ownerUserId, NotificationType.BOOKING_CANCELLED,
                    "Booking #" + b.getId() + " at " + hotelName
                            + " has been marked as No-Show. Guest did not check in. "
                            + "The room has been released back to availability.");
        } catch (Exception e) {
            log.debug("Failed to send no-show notifications for booking {}", b.getId());
        }
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
