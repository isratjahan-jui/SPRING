package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.BookingMapperDTO;
import com.MHM.MultiHotelManagement.dto.request.BookingRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.BookingResponseDTO;
import com.MHM.MultiHotelManagement.entity.Booking;
import com.MHM.MultiHotelManagement.entity.Customer;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.entity.Room;
import com.MHM.MultiHotelManagement.entity.FoodItem;
import com.MHM.MultiHotelManagement.entity.ExtraService;
import com.MHM.MultiHotelManagement.entity.HotelExtraService;
import com.MHM.MultiHotelManagement.entity.HotelDetails;
import com.MHM.MultiHotelManagement.entity.Payment;
import com.MHM.MultiHotelManagement.entity.Commission;
import com.MHM.MultiHotelManagement.entity.Wallet;
import com.MHM.MultiHotelManagement.entity.WalletTransaction;
import com.MHM.MultiHotelManagement.enums.BookingStatus;
import com.MHM.MultiHotelManagement.enums.PaymentStatus;
import com.MHM.MultiHotelManagement.enums.ServiceStatus;
import com.MHM.MultiHotelManagement.repository.BookingRepository;
import com.MHM.MultiHotelManagement.repository.CustomerRepository;
import com.MHM.MultiHotelManagement.repository.HotelRepository;
import com.MHM.MultiHotelManagement.repository.RoomRepository;
import com.MHM.MultiHotelManagement.repository.FoodItemRepository;
import com.MHM.MultiHotelManagement.repository.HotelExtraServiceRepository;
import com.MHM.MultiHotelManagement.repository.HotelDetailsRepository;
import com.MHM.MultiHotelManagement.repository.PaymentRepository;
import com.MHM.MultiHotelManagement.repository.CommissionRepository;
import com.MHM.MultiHotelManagement.repository.WalletRepository;
import com.MHM.MultiHotelManagement.repository.WalletTransactionRepository;
import com.MHM.MultiHotelManagement.service.BookingService;
import com.MHM.MultiHotelManagement.service.NotificationService;
import com.MHM.MultiHotelManagement.dto.request.NotificationRequestDTO;
import com.MHM.MultiHotelManagement.enums.NotificationChannel;
import com.MHM.MultiHotelManagement.enums.NotificationType;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingServiceImple implements BookingService {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final FoodItemRepository foodItemRepository;
    private final HotelExtraServiceRepository hotelExtraServiceRepository;
    private final HotelDetailsRepository hotelDetailsRepository;
    private final PaymentRepository paymentRepository;
    private final CommissionRepository commissionRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final NotificationService notificationService;

    @Value("${image.upload.dir:uploads}")
    private String uploadDir;

    public BookingServiceImple(BookingRepository bookingRepository,
                               CustomerRepository customerRepository,
                               HotelRepository hotelRepository,
                               RoomRepository roomRepository,
                               FoodItemRepository foodItemRepository,
                               HotelExtraServiceRepository hotelExtraServiceRepository,
                               HotelDetailsRepository hotelDetailsRepository,
                               PaymentRepository paymentRepository,
                               CommissionRepository commissionRepository,
                               WalletRepository walletRepository,
                               WalletTransactionRepository walletTransactionRepository,
                               NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.foodItemRepository = foodItemRepository;
        this.hotelExtraServiceRepository = hotelExtraServiceRepository;
        this.hotelDetailsRepository = hotelDetailsRepository;
        this.paymentRepository = paymentRepository;
        this.commissionRepository = commissionRepository;
        this.walletRepository = walletRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO dto) {
        Booking booking = BookingMapperDTO.toEntity(dto);

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found"));
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));

        booking.setCustomer(customer);
        booking.setHotel(hotel);
        booking.setRoom(room);
        booking.setStatus(BookingStatus.PENDING);

        // Check room availability for selected dates
        int bookedForDates = bookingRepository.countBookedRoomsForDates(
                room.getId(), dto.getCheckInDate(), dto.getCheckOutDate());
        int availableForDates = room.getTotalRooms() - bookedForDates;
        if (dto.getNumberOfRooms() > availableForDates) {
            throw new IllegalStateException(
                    "Not enough rooms available for the selected dates. Available: " + availableForDates + ", Requested: " + dto.getNumberOfRooms());
        }

        // Calculate number of nights
        long diffMs = dto.getCheckOutDate().getTime() - dto.getCheckInDate().getTime();
        int numberOfNights = Math.max(1, (int) Math.ceil(diffMs / (1000.0 * 60 * 60 * 24)));

        BigDecimal roomTotal = BigDecimal.valueOf(room.getPricePerNight())
                .multiply(BigDecimal.valueOf(dto.getNumberOfRooms()))
                .multiply(BigDecimal.valueOf(numberOfNights));
        BigDecimal discount = roomTotal.multiply(dto.getDiscountRate())
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
        BigDecimal totalAmount = roomTotal.subtract(discount);
        booking.setTotalPrice(roomTotal);
        booking.setTotalAmount(totalAmount);
        booking.setDueAmount(totalAmount.subtract(dto.getAdvanceAmount()));

        // Set cancellation deadline: 24 hours before check-in
        if (dto.getCheckInDate() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dto.getCheckInDate());
            cal.add(Calendar.HOUR, -24);
            booking.setCancellationDeadline(cal.getTime());
            booking.setCancellationPolicyText("Free cancellation up to 24 hours before check-in");
        }

        // FoodItem integration
        if (dto.getFoodItemIds() != null && !dto.getFoodItemIds().isEmpty()) {
            List<FoodItem> foodItems = foodItemRepository.findAllById(dto.getFoodItemIds());
            booking.setFoodItems(foodItems);
        }

        // Extra Service integration
        BigDecimal extraServicesTotal = BigDecimal.ZERO;
        if (dto.getExtraServiceIds() != null && !dto.getExtraServiceIds().isEmpty()) {
            List<HotelExtraService> definitions = hotelExtraServiceRepository.findAllById(dto.getExtraServiceIds());
            for (HotelExtraService def : definitions) {
                ExtraService es = new ExtraService();
                es.setServiceType(def.getServiceName());
                es.setPrice(def.getPrice());
                es.setServiceStatus(ServiceStatus.PENDING);
                es.setBooking(booking);
                booking.addExtraService(es);
                extraServicesTotal = extraServicesTotal.add(BigDecimal.valueOf(def.getPrice()));
            }
        }

        // Add extra services cost to total
        if (extraServicesTotal.compareTo(BigDecimal.ZERO) > 0) {
            booking.setTotalAmount(totalAmount.add(extraServicesTotal));
            booking.setDueAmount(totalAmount.subtract(dto.getAdvanceAmount()).add(extraServicesTotal));
        }

        Booking saved = bookingRepository.save(booking);

        // Update room availability counters
        room.setAvailableRooms(room.getAvailableRooms() - dto.getNumberOfRooms());
        room.setBookedRooms(room.getBookedRooms() + dto.getNumberOfRooms());
        if (room.getAvailableRooms() <= 0) {
            room.setIsAvailable(false);
        }
        roomRepository.save(room);

        // Send notifications
        try {
            NotificationRequestDTO customerNotification = new NotificationRequestDTO();
            customerNotification.setUserId(customer.getUser().getId());
            customerNotification.setType(NotificationType.BOOKING_CONFIRMED);
            customerNotification.setChannel(NotificationChannel.WEB);
            customerNotification.setMessage("Booking request submitted at " + hotel.getHotelName() + ". Awaiting confirmation. Booking ID: #" + saved.getId());
            notificationService.createNotification(customerNotification);

            NotificationRequestDTO ownerNotification = new NotificationRequestDTO();
            ownerNotification.setUserId(hotel.getOwner().getUser().getId());
            ownerNotification.setType(NotificationType.BOOKING_CONFIRMED);
            ownerNotification.setChannel(NotificationChannel.WEB);
            ownerNotification.setMessage("New booking request from " + customer.getCustomerName() + " at " + hotel.getHotelName() + ". Booking ID: #" + saved.getId());
            notificationService.createNotification(ownerNotification);
        } catch (Exception ignored) {}

        return BookingMapperDTO.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public BookingResponseDTO updateBooking(Long id, BookingRequestDTO dto) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        Room room = booking.getRoom();
        int oldNumberOfRooms = booking.getNumberOfRooms();
        int newNumberOfRooms = dto.getNumberOfRooms();
        int roomDiff = newNumberOfRooms - oldNumberOfRooms;

        booking.setCheckInDate(dto.getCheckInDate());
        booking.setCheckOutDate(dto.getCheckOutDate());
        booking.setNumberOfRooms(newNumberOfRooms);
        booking.setTotalGuests(dto.getTotalGuests());
        booking.setDiscountRate(dto.getDiscountRate());
        booking.setAdvanceAmount(dto.getAdvanceAmount());

        // FoodItem integration update
        if (dto.getFoodItemIds() != null) {
            List<FoodItem> foodItems = foodItemRepository.findAllById(dto.getFoodItemIds());
            booking.setFoodItems(foodItems);
        }

        Booking updated = bookingRepository.save(booking);

        // Adjust room availability counters if number of rooms changed
        if (roomDiff != 0) {
            int newAvailable = room.getAvailableRooms() - roomDiff;
            if (newAvailable < 0) {
                throw new IllegalStateException("Not enough rooms available. Requested: " + (-roomDiff) + ", Available: " + room.getAvailableRooms());
            }
            room.setAvailableRooms(newAvailable);
            room.setBookedRooms(room.getBookedRooms() + roomDiff);
            room.setIsAvailable(room.getAvailableRooms() > 0);
            roomRepository.save(room);
        }

        return BookingMapperDTO.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        return BookingMapperDTO.toResponseDTO(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getBookingsByCustomer(Long customerId) {
        return bookingRepository.findBookingsByCustomerId(customerId)
                .stream().map(BookingMapperDTO::toResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getBookingsByHotel(Long hotelId) {
        return bookingRepository.findBookingsByHotelId(hotelId)
                .stream().map(BookingMapperDTO::toResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getBookingsByRoom(Long roomId) {
        return bookingRepository.findBookingsByRoomId(roomId)
                .stream().map(BookingMapperDTO::toResponseDTO).toList();
    }

    @Override
    @Transactional
    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        // Restore room availability
        Room room = booking.getRoom();
        room.setAvailableRooms(room.getAvailableRooms() + booking.getNumberOfRooms());
        room.setBookedRooms(Math.max(0, room.getBookedRooms() - booking.getNumberOfRooms()));
        room.setIsAvailable(true);
        roomRepository.save(room);

        bookingRepository.deleteById(id);
    }

    @Override
    @Transactional
    public BookingResponseDTO addFoodItemsToBooking(Long bookingId, List<Long> foodItemIds) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        List<FoodItem> foodItems = foodItemRepository.findAllById(foodItemIds);
        booking.getFoodItems().addAll(foodItems);

        Booking updated = bookingRepository.save(booking);
        return BookingMapperDTO.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public BookingResponseDTO cancelFoodItemsFromBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        List<FoodItem> modifiedFoodItems = new java.util.ArrayList<>();
        booking.getFoodItems().forEach(foodItem -> {
            if (foodItem.getCancellableUntil() != null &&
                    LocalDateTime.now().isBefore(foodItem.getCancellableUntil())) {
                foodItem.setCancelled(true);
                foodItem.setCancelledAt(LocalDateTime.now());
                modifiedFoodItems.add(foodItem);
            }
        });

        if (!modifiedFoodItems.isEmpty()) {
            foodItemRepository.saveAll(modifiedFoodItems);
        }

        return BookingMapperDTO.toResponseDTO(booking);
    }

    @Override
    @Transactional
    public BookingResponseDTO updateBookingStatus(Long id, String status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        BookingStatus newStatus = BookingStatus.valueOf(status);

        // Restore room availability when cancelling
        if (newStatus == BookingStatus.CANCELLED && booking.getStatus() != BookingStatus.CANCELLED) {
            Room room = booking.getRoom();
            room.setAvailableRooms(room.getAvailableRooms() + booking.getNumberOfRooms());
            room.setBookedRooms(room.getBookedRooms() - booking.getNumberOfRooms());
            if (room.getAvailableRooms() > 0) {
                room.setIsAvailable(true);
            }
            roomRepository.save(room);
        }

        booking.setStatus(newStatus);
        Booking updated = bookingRepository.save(booking);

        // Send notifications based on status change
        try {
            String hotelName = booking.getHotel().getHotelName();
            Long customerUserId = booking.getCustomer().getUser().getId();
            Long ownerUserId = booking.getHotel().getOwner().getUser().getId();

            switch (newStatus) {
                case CONFIRMED -> {
                    sendNotificationToUser(customerUserId, NotificationType.BOOKING_CONFIRMED,
                            "Your booking at " + hotelName + " has been confirmed. Booking ID: #" + booking.getId());
                    sendNotificationToUser(ownerUserId, NotificationType.BOOKING_CONFIRMED,
                            "Booking #" + booking.getId() + " has been confirmed for " + hotelName);
                }
                case CANCELLED -> {
                    sendNotificationToUser(customerUserId, NotificationType.BOOKING_CANCELLED,
                            "Your booking at " + hotelName + " has been cancelled. Booking ID: #" + booking.getId());
                    sendNotificationToUser(ownerUserId, NotificationType.BOOKING_CANCELLED,
                            "Booking #" + booking.getId() + " has been cancelled for " + hotelName);
                }
                case CHECKED_IN -> {
                    sendNotificationToUser(customerUserId, NotificationType.BOOKING_REMINDER,
                            "Welcome! You have successfully checked in at " + hotelName + ". Booking ID: #" + booking.getId());
                    sendNotificationToUser(ownerUserId, NotificationType.BOOKING_REMINDER,
                            "Guest " + booking.getContractPersonName() + " has checked in at " + hotelName);
                }
                case CHECKED_OUT -> {
                    sendNotificationToUser(customerUserId, NotificationType.BOOKING_REMINDER,
                            "You have successfully checked out from " + hotelName + ". Booking ID: #" + booking.getId());
                    sendNotificationToUser(ownerUserId, NotificationType.BOOKING_REMINDER,
                            "Guest " + booking.getContractPersonName() + " has checked out from " + hotelName);
                }
                default -> {}
            }
        } catch (Exception ignored) {}

        return BookingMapperDTO.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public BookingResponseDTO onlineCheckIn(Long bookingId, MultipartFile idImage) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Booking must be CONFIRMED before online check-in");
        }

        if (idImage != null && !idImage.isEmpty()) {
            String imagePath = uploadIdImage(idImage, booking.getId());
            booking.setIdImagePath(imagePath);
        }

        booking.setOnlineCheckIn(true);
        booking.setStatus(BookingStatus.CHECKED_IN);

        // Generate digital key: bookingId-roomId-randomUUID
        String key = "DK-" + booking.getId() + "-" + booking.getRoom().getId() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        booking.setDigitalKey(key);

        Booking updated = bookingRepository.save(booking);

        // Send check-in notifications
        try {
            String hotelName = booking.getHotel().getHotelName();
            sendNotificationToUser(booking.getCustomer().getUser().getId(), NotificationType.BOOKING_REMINDER,
                    "Online check-in successful at " + hotelName + ". Your digital key is: " + key);
            sendNotificationToUser(booking.getHotel().getOwner().getUser().getId(), NotificationType.BOOKING_REMINDER,
                    "Guest " + booking.getContractPersonName() + " has completed online check-in at " + hotelName);
        } catch (Exception ignored) {}

        return BookingMapperDTO.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public BookingResponseDTO expressCheckOut(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new IllegalStateException("Booking must be CHECKED_IN before check-out");
        }

        booking.setStatus(BookingStatus.CHECKED_OUT);

        // Restore room availability on checkout
        Room room = booking.getRoom();
        room.setAvailableRooms(room.getAvailableRooms() + booking.getNumberOfRooms());
        room.setBookedRooms(Math.max(0, room.getBookedRooms() - booking.getNumberOfRooms()));
        room.setIsAvailable(true);
        roomRepository.save(room);

        Booking updated = bookingRepository.save(booking);

        // Send check-out notifications
        try {
            String hotelName = booking.getHotel().getHotelName();
            sendNotificationToUser(booking.getCustomer().getUser().getId(), NotificationType.BOOKING_REMINDER,
                    "Express check-out successful from " + hotelName + ". Thank you for staying with us! Booking ID: #" + bookingId);
            sendNotificationToUser(booking.getHotel().getOwner().getUser().getId(), NotificationType.BOOKING_REMINDER,
                    "Guest " + booking.getContractPersonName() + " has checked out from " + hotelName);
        } catch (Exception ignored) {}

        return BookingMapperDTO.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getAllBookings() {
        return bookingRepository.findAllWithDetails()
                .stream().map(BookingMapperDTO::toResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getBookingsByOwner(Long ownerId) {
        return bookingRepository.findAllBookingsByOwnerId(ownerId)
                .stream().map(BookingMapperDTO::toResponseDTO).toList();
    }

    @Override
    @Transactional
    public BookingResponseDTO markNoShow(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        if (booking.getStatus() != BookingStatus.CONFIRMED && booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only CONFIRMED or PENDING bookings can be marked as No-Show");
        }

        booking.setStatus(BookingStatus.NO_SHOW);

        // Restore room availability on no-show
        Room room = booking.getRoom();
        room.setAvailableRooms(room.getAvailableRooms() + booking.getNumberOfRooms());
        room.setBookedRooms(Math.max(0, room.getBookedRooms() - booking.getNumberOfRooms()));
        room.setIsAvailable(true);
        roomRepository.save(room);

        Booking updated = bookingRepository.save(booking);

        // Send no-show notifications
        try {
            String hotelName = booking.getHotel().getHotelName();
            sendNotificationToUser(booking.getCustomer().getUser().getId(), NotificationType.BOOKING_CANCELLED,
                    "Your booking at " + hotelName + " has been marked as No-Show. Booking ID: #" + bookingId);
            sendNotificationToUser(booking.getHotel().getOwner().getUser().getId(), NotificationType.BOOKING_CANCELLED,
                    "Booking #" + bookingId + " has been marked as No-Show for " + hotelName);
        } catch (Exception ignored) {}

        return BookingMapperDTO.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public BookingResponseDTO addExtraCharges(Long bookingId, double amount) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        BigDecimal extraCharge = BigDecimal.valueOf(amount);
        booking.setExtraCharges(booking.getExtraCharges().add(extraCharge));
        booking.setDueAmount(booking.getDueAmount().add(extraCharge));
        booking.setTotalAmount(booking.getTotalAmount().add(extraCharge));
        Booking updated = bookingRepository.save(booking);
        return BookingMapperDTO.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public BookingResponseDTO cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }
        if (booking.getStatus() == BookingStatus.CHECKED_OUT) {
            throw new IllegalStateException("Cannot cancel a checked-out booking");
        }

        HotelDetails hotelDetails = hotelDetailsRepository.findByHotel_Id(booking.getHotel().getId()).orElse(null);
        String refundPolicy = hotelDetails != null ? hotelDetails.getCancellationDepositRefundable() : "FULL_REFUND";

        BigDecimal advancePaid = booking.getAdvanceAmount() != null ? booking.getAdvanceAmount() : BigDecimal.ZERO;
        BigDecimal refundAmount = BigDecimal.ZERO;
        BigDecimal commissionRetained = BigDecimal.ZERO;
        String refundNote = "";

        if (advancePaid.compareTo(BigDecimal.ZERO) > 0) {
            switch (refundPolicy) {
                case "FULL_REFUND":
                    refundAmount = advancePaid;
                    commissionRetained = BigDecimal.ZERO;
                    refundNote = "Full refund: 100% of deposit returned.";
                    break;

                case "PARTIAL_REFUND":
                    refundAmount = advancePaid.multiply(BigDecimal.valueOf(0.5)).setScale(2, RoundingMode.HALF_UP);
                    commissionRetained = advancePaid.subtract(refundAmount);
                    refundNote = "Partial refund: 50% returned, 50% retained as commission.";
                    break;

                case "CONDITIONAL_REFUND":
                    boolean isEarlyCancel = booking.getCancellationDeadline() != null
                            && new Date().before(booking.getCancellationDeadline());
                    if (isEarlyCancel) {
                        refundAmount = advancePaid;
                        commissionRetained = BigDecimal.ZERO;
                        refundNote = "Conditional refund: Early cancellation, full refund.";
                    } else {
                        refundAmount = advancePaid.multiply(BigDecimal.valueOf(0.3)).setScale(2, RoundingMode.HALF_UP);
                        commissionRetained = advancePaid.subtract(refundAmount);
                        refundNote = "Conditional refund: Late cancellation, 30% returned, 70% retained.";
                    }
                    break;

                case "NON_REFUNDABLE":
                default:
                    refundAmount = BigDecimal.ZERO;
                    commissionRetained = advancePaid;
                    refundNote = "Non-refundable: No deposit refund on cancellation.";
                    break;
            }
        }

        // Restore room availability
        Room room = booking.getRoom();
        room.setAvailableRooms(room.getAvailableRooms() + booking.getNumberOfRooms());
        room.setBookedRooms(room.getBookedRooms() - booking.getNumberOfRooms());
        if (room.getAvailableRooms() > 0) {
            room.setIsAvailable(true);
        }
        roomRepository.save(room);

        // Update payment status to REFUNDED if there's a refund
        Optional<Payment> paymentOpt = paymentRepository.findByBooking_Id(bookingId);
        if (paymentOpt.isPresent() && refundAmount.compareTo(BigDecimal.ZERO) > 0) {
            Payment payment = paymentOpt.get();
            if (payment.getStatus() == PaymentStatus.PAID) {
                payment.setStatus(PaymentStatus.REFUNDED);
                paymentRepository.save(payment);
            }
        }

        // Handle commission retention for partial/conditional/non-refundable
        if (commissionRetained.compareTo(BigDecimal.ZERO) > 0) {
            Commission retainedCommission = new Commission();
            retainedCommission.setBooking(booking);
            retainedCommission.setCommissionRate(10.0);
            retainedCommission.setAdminEarnings(commissionRetained.doubleValue());
            retainedCommission.setHotelOwnerEarnings(0.0);
            paymentOpt.ifPresent(retainedCommission::setPayment);
            commissionRepository.save(retainedCommission);
        }

        // Credit refund to customer wallet
        if (refundAmount.compareTo(BigDecimal.ZERO) > 0 && booking.getCustomer() != null) {
            Wallet wallet = walletRepository.findByUser_Id(booking.getCustomer().getUser().getId())
                    .orElseGet(() -> {
                        Wallet newWallet = new Wallet();
                        newWallet.setUser(booking.getCustomer().getUser());
                        return walletRepository.save(newWallet);
                    });

            wallet.setBalance(wallet.getBalance().add(refundAmount));
            walletRepository.save(wallet);

            WalletTransaction transaction = new WalletTransaction();
            transaction.setWallet(wallet);
            transaction.setAmount(refundAmount);
            transaction.setType("CREDIT");
            transaction.setDescription("Refund for cancelled booking #" + bookingId + " - " + refundNote);
            transaction.setReferenceId(bookingId);
            walletTransactionRepository.save(transaction);
        }

        booking.setAdvanceAmount(advancePaid.subtract(refundAmount));
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationPolicyText(refundNote);
        Booking updated = bookingRepository.save(booking);

        // Send cancellation notifications
        try {
            String hotelName = booking.getHotel().getHotelName();
            Long customerUserId = booking.getCustomer().getUser().getId();
            Long ownerUserId = booking.getHotel().getOwner().getUser().getId();

            String customerMsg = "Your booking at " + hotelName + " has been cancelled. Booking ID: #" + bookingId;
            if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
                customerMsg += " Refund of ৳" + refundAmount + " has been credited to your wallet.";
            }
            sendNotificationToUser(customerUserId, NotificationType.BOOKING_CANCELLED, customerMsg);
            sendNotificationToUser(ownerUserId, NotificationType.BOOKING_CANCELLED,
                    "Booking #" + bookingId + " has been cancelled for " + hotelName + ". Refund note: " + refundNote);
        } catch (Exception ignored) {}

        return BookingMapperDTO.toResponseDTO(updated);
    }

    private String uploadIdImage(MultipartFile file, Long bookingId) {
        try {
            Path path = Paths.get(uploadDir, "checkin-id");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }

            String fileName = "booking_" + bookingId + "_" + UUID.randomUUID() + ext;
            Files.copy(file.getInputStream(), path.resolve(fileName));
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("ID image upload failed: " + e.getMessage());
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
