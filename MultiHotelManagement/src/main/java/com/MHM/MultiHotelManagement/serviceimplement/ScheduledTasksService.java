package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.entity.Booking;
import com.MHM.MultiHotelManagement.entity.Commission;
import com.MHM.MultiHotelManagement.entity.HotelDetails;
import com.MHM.MultiHotelManagement.entity.Payment;
import com.MHM.MultiHotelManagement.entity.Room;
import com.MHM.MultiHotelManagement.entity.Wallet;
import com.MHM.MultiHotelManagement.entity.WalletTransaction;
import com.MHM.MultiHotelManagement.enums.BookingStatus;
import com.MHM.MultiHotelManagement.enums.PaymentStatus;
import com.MHM.MultiHotelManagement.repository.BookingRepository;
import com.MHM.MultiHotelManagement.repository.CommissionRepository;
import com.MHM.MultiHotelManagement.repository.HotelDetailsRepository;
import com.MHM.MultiHotelManagement.repository.PaymentRepository;
import com.MHM.MultiHotelManagement.repository.RoomRepository;
import com.MHM.MultiHotelManagement.repository.WalletRepository;
import com.MHM.MultiHotelManagement.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduledTasksService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasksService.class);
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final CommissionRepository commissionRepository;
    private final PaymentRepository paymentRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final HotelDetailsRepository hotelDetailsRepository;
}
