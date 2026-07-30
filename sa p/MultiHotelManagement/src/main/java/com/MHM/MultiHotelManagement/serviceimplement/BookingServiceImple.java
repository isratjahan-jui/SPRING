package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.BookingMapperDTO;
import com.MHM.MultiHotelManagement.dto.request.BookingRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.BookingResponseDTO;
import com.MHM.MultiHotelManagement.dto.response.CheckInCheckOutResponseDTO;
import com.MHM.MultiHotelManagement.entity.Booking;
import com.MHM.MultiHotelManagement.entity.BookingRoom;
import com.MHM.MultiHotelManagement.entity.Customer;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.entity.Invoice;
import com.MHM.MultiHotelManagement.entity.Room;
import com.MHM.MultiHotelManagement.entity.FoodItem;
import com.MHM.MultiHotelManagement.entity.ExtraService;
import com.MHM.MultiHotelManagement.entity.HotelExtraService;
import com.MHM.MultiHotelManagement.entity.HotelDetails;
import com.MHM.MultiHotelManagement.entity.Payment;
import com.MHM.MultiHotelManagement.entity.Commission;
import com.MHM.MultiHotelManagement.entity.HotelOwner;
import com.MHM.MultiHotelManagement.enums.BookingStatus;
import com.MHM.MultiHotelManagement.enums.InvoiceStatus;
import com.MHM.MultiHotelManagement.enums.InvoiceType;
import com.MHM.MultiHotelManagement.enums.PaymentStatus;
import com.MHM.MultiHotelManagement.enums.PaymentType;
import com.MHM.MultiHotelManagement.enums.ServiceStatus;
import com.MHM.MultiHotelManagement.repository.BookingRepository;
import com.MHM.MultiHotelManagement.repository.BookingRoomRepository;
import com.MHM.MultiHotelManagement.repository.CustomerRepository;
import com.MHM.MultiHotelManagement.repository.HotelOwnerRepository;
import com.MHM.MultiHotelManagement.repository.HotelRepository;
import com.MHM.MultiHotelManagement.repository.InvoiceRepository;
import com.MHM.MultiHotelManagement.repository.RoomRepository;
import com.MHM.MultiHotelManagement.repository.FoodItemRepository;
import com.MHM.MultiHotelManagement.repository.HotelExtraServiceRepository;
import com.MHM.MultiHotelManagement.repository.HotelDetailsRepository;
import com.MHM.MultiHotelManagement.repository.PaymentRepository;
import com.MHM.MultiHotelManagement.repository.CommissionRepository;
import com.MHM.MultiHotelManagement.repository.CouponRepository;
import com.MHM.MultiHotelManagement.entity.Coupon;
import com.MHM.MultiHotelManagement.service.BookingService;
import com.MHM.MultiHotelManagement.service.NotificationService;
import com.MHM.MultiHotelManagement.service.AuditTrailService;
import com.MHM.MultiHotelManagement.service.WalletService;
import com.MHM.MultiHotelManagement.util.FileUploadUtil;
import com.MHM.MultiHotelManagement.util.OwnershipGuard;
import com.MHM.MultiHotelManagement.dto.request.NotificationRequestDTO;
import com.MHM.MultiHotelManagement.enums.NotificationChannel;
import com.MHM.MultiHotelManagement.enums.NotificationType;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.stream.Collectors;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingServiceImple implements BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImple.class);

    private final BookingRepository bookingRepository;
    private final BookingRoomRepository bookingRoomRepository;
    private final CustomerRepository customerRepository;
    private final HotelRepository hotelRepository;
    private final HotelOwnerRepository hotelOwnerRepository;
    private final RoomRepository roomRepository;
    private final FoodItemRepository foodItemRepository;
    private final HotelExtraServiceRepository hotelExtraServiceRepository;
    private final HotelDetailsRepository hotelDetailsRepository;
    private final PaymentRepository paymentRepository;
    private final CommissionRepository commissionRepository;
    private final CouponRepository couponRepository;
    private final WalletService walletService;
    private final InvoiceRepository invoiceRepository;
    private final NotificationService notificationService;
    private final AuditTrailService auditTrailService;
    private final OwnershipGuard ownershipGuard;

    @Value("${image.upload.dir:uploads}")
    private String uploadDir;

    public BookingServiceImple(BookingRepository bookingRepository,
                               BookingRoomRepository bookingRoomRepository,
                               CustomerRepository customerRepository,
                               HotelRepository hotelRepository,
                               HotelOwnerRepository hotelOwnerRepository,
                               RoomRepository roomRepository,
                               FoodItemRepository foodItemRepository,
                               HotelExtraServiceRepository hotelExtraServiceRepository,
                               HotelDetailsRepository hotelDetailsRepository,
                               PaymentRepository paymentRepository,
                               CommissionRepository commissionRepository,
                               CouponRepository couponRepository,
                               WalletService walletService,
                               InvoiceRepository invoiceRepository,
                               NotificationService notificationService,
                               AuditTrailService auditTrailService,
                               OwnershipGuard ownershipGuard) {
        this.bookingRepository = bookingRepository;
        this.bookingRoomRepository = bookingRoomRepository;
        this.customerRepository = customerRepository;
        this.hotelRepository = hotelRepository;
        this.hotelOwnerRepository = hotelOwnerRepository;
        this.roomRepository = roomRepository;
        this.foodItemRepository = foodItemRepository;
        this.hotelExtraServiceRepository = hotelExtraServiceRepository;
        this.hotelDetailsRepository = hotelDetailsRepository;
        this.paymentRepository = paymentRepository;
        this.commissionRepository = commissionRepository;
        this.couponRepository = couponRepository;
        this.walletService = walletService;
        this.invoiceRepository = invoiceRepository;
        this.notificationService = notificationService;
        this.auditTrailService = auditTrailService;
        this.ownershipGuard = ownershipGuard;
    }

    @Override
    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO dto) {
        validateDates(dto.getCheckInDate(), dto.getCheckOutDate());

        Booking booking = BookingMapperDTO.toEntity(dto);

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found"));
        // Pessimistic lock — held until this transaction commits, so a second
        // concurrent booking for the same room blocks here instead of reading
        // the same stale availableRooms count and overselling the room.
        Room room = roomRepository.findByIdForUpdate(dto.getRoomId())
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

        // Apply an optional coupon on top of any booking-level discount rate
        BigDecimal couponDiscount = applyCoupon(dto.getCouponCode(), dto.getHotelId(), roomTotal);

        BigDecimal totalAmount = roomTotal.subtract(discount).subtract(couponDiscount).max(BigDecimal.ZERO);
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

        // Calculate and set discount/tax/net amounts NOW (before save)
        BigDecimal finalTotal = booking.getTotalAmount();
        BigDecimal discountRate = booking.getDiscountRate() != null ? booking.getDiscountRate() : BigDecimal.ZERO;
        BigDecimal discountAmt = finalTotal.multiply(discountRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal taxAmt = finalTotal.subtract(discountAmt).multiply(BigDecimal.valueOf(0.15)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal netAmt = finalTotal.add(taxAmt).subtract(discountAmt);
        booking.setDiscountAmount(discountAmt);
        booking.setTaxAmount(taxAmt);
        booking.setNetAmount(netAmt);

        Booking saved = bookingRepository.save(booking);

        // Auto-create BookingRoom entry
        try {
            BookingRoom bookingRoom = new BookingRoom();
            bookingRoom.setBooking(saved);
            bookingRoom.setRoom(room);
            bookingRoom.setNumberOfRooms(dto.getNumberOfRooms());
            bookingRoom.setAdults(dto.getTotalGuests());
            bookingRoom.setChildren(0);
            bookingRoom.setPrice(room.getPricePerNight() * dto.getNumberOfRooms());
            bookingRoomRepository.save(bookingRoom);
            log.info("BookingRoom created for booking {}", saved.getId());
        } catch (Exception e) {
            log.error("Failed to create BookingRoom for booking {}: {}", saved.getId(), e.getMessage());
        }

        // Update room availability counters
        room.setAvailableRooms(room.getAvailableRooms() - dto.getNumberOfRooms());
        room.setBookedRooms(room.getBookedRooms() + dto.getNumberOfRooms());
        if (room.getAvailableRooms() <= 0) {
            room.setIsAvailable(false);
        }
        roomRepository.save(room);

        // Capture values for lambda
        final Long customerId = customer.getUser().getId();
        final Long ownerUserId = hotel.getOwner().getUser().getId();
        final String hotelName = hotel.getHotelName();
        final String customerName = customer.getCustomerName();

        // Schedule invoice generation + notifications AFTER transaction commits successfully
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // Auto-generate invoice at booking time
                try {
                    generateInvoiceAtBooking(saved);
                    log.info("Invoice generated at booking time for booking {}", saved.getId());
                } catch (Exception e) {
                    log.error("Invoice generation failed for booking {}: {}", saved.getId(), e.getMessage(), e);
                }

                // Send notifications
                try {
                    NotificationRequestDTO customerNotification = new NotificationRequestDTO();
                    customerNotification.setUserId(customerId);
                    customerNotification.setType(NotificationType.BOOKING_CONFIRMED);
                    customerNotification.setChannel(NotificationChannel.WEB);
                    customerNotification.setMessage("Booking request submitted at " + hotelName + ". Awaiting confirmation. Booking ID: #" + saved.getId());
                    notificationService.createNotification(customerNotification);

                    NotificationRequestDTO ownerNotification = new NotificationRequestDTO();
                    ownerNotification.setUserId(ownerUserId);
                    ownerNotification.setType(NotificationType.BOOKING_CONFIRMED);
                    ownerNotification.setChannel(NotificationChannel.WEB);
                    ownerNotification.setMessage("New booking request from " + customerName + " at " + hotelName + ". Booking ID: #" + saved.getId());
                    notificationService.createNotification(ownerNotification);
                } catch (Exception ignored) {}
            }
        });

        return BookingMapperDTO.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public BookingResponseDTO updateBooking(Long id, BookingRequestDTO dto) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        ownershipGuard.verifyHotelOwnership(booking.getHotel());
        validateDates(dto.getCheckInDate(), dto.getCheckOutDate());

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
        ownershipGuard.verifyBookingAccess(booking);
        return BookingMapperDTO.toResponseDTO(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getBookingsByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        ownershipGuard.verifyCustomerAccess(customer);
        return bookingRepository.findBookingsByCustomerId(customerId)
                .stream().map(BookingMapperDTO::toResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getBookingsByHotel(Long hotelId) {
        // Hotel owners may only list bookings for their own hotel; customers/admins are
        // unrestricted here since this isn't a hotel-owner-only management endpoint.
        if (ownershipGuard.hasRole("HOTEL_OWNER")) {
            Hotel hotel = hotelRepository.findById(hotelId)
                    .orElseThrow(() -> new EntityNotFoundException("Hotel not found"));
            ownershipGuard.verifyHotelOwnership(hotel);
        }
        return bookingRepository.findBookingsByHotelId(hotelId)
                .stream().map(BookingMapperDTO::toResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getBookingsByRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));
        ownershipGuard.verifyHotelOwnership(room.getHotel());
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
        ownershipGuard.verifyBookingAccess(booking);

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
        ownershipGuard.verifyBookingAccess(booking);

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
        ownershipGuard.verifyHotelOwnership(booking.getHotel());

        BookingStatus newStatus = BookingStatus.valueOf(status);

        // A booking whose room was already restored (NO_SHOW/EXPIRED via the expiry
        // job, or CHECKED_OUT) must not restore it again on a later CANCELLED
        // transition — that would inflate available inventory.
        boolean roomAlreadyRestored = booking.getStatus() == BookingStatus.CANCELLED
                || booking.getStatus() == BookingStatus.NO_SHOW
                || booking.getStatus() == BookingStatus.EXPIRED
                || booking.getStatus() == BookingStatus.CHECKED_OUT;

        // Restore room availability when the booking leaves an active state —
        // cancelling OR checking out both free the held rooms. Guard against a
        // second restore if the room was already given back.
        boolean releasesRoom = newStatus == BookingStatus.CANCELLED
                || newStatus == BookingStatus.CHECKED_OUT;
        if (releasesRoom && !roomAlreadyRestored) {
            Room room = booking.getRoom();
            room.setAvailableRooms(room.getAvailableRooms() + booking.getNumberOfRooms());
            room.setBookedRooms(Math.max(0, room.getBookedRooms() - booking.getNumberOfRooms()));
            if (room.getAvailableRooms() > 0) {
                room.setIsAvailable(true);
            }
            roomRepository.save(room);
        }

        booking.setStatus(newStatus);
        Booking updated = bookingRepository.save(booking);

        if (newStatus == BookingStatus.CHECKED_OUT) {
            try {
                generateInvoiceAtCheckout(booking);
                log.info("Checkout invoice generated for booking {}", booking.getId());
            } catch (Exception e) {
                log.error("Checkout invoice generation failed for booking {}: {}", booking.getId(), e.getMessage(), e);
            }

            try {
                generateCommissionAtCheckout(booking);
            } catch (Exception e) {
                log.error("Commission generation failed for booking {}: {}", booking.getId(), e.getMessage(), e);
            }
        }

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
        ownershipGuard.verifyBookingAccess(booking);

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
        ownershipGuard.verifyBookingAccess(booking);

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

        try {
            generateInvoiceAtCheckout(booking);
            log.info("Checkout invoice generated for booking {}", bookingId);
        } catch (Exception e) {
            log.error("Checkout invoice generation failed for booking {}: {}", bookingId, e.getMessage(), e);
        }

        try {
            generateCommissionAtCheckout(booking);
        } catch (Exception e) {
            log.error("Commission generation failed for booking {}: {}", bookingId, e.getMessage(), e);
        }

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
        HotelOwner owner = hotelOwnerRepository.findByIdWithUser(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Hotel owner not found"));
        ownershipGuard.verifyHotelOwnerAccess(owner);
        return bookingRepository.findAllBookingsByOwnerId(ownerId)
                .stream().map(BookingMapperDTO::toResponseDTO).toList();
    }

    @Override
    @Transactional
    public BookingResponseDTO markNoShow(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        ownershipGuard.verifyHotelOwnership(booking.getHotel());

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
        ownershipGuard.verifyHotelOwnership(booking.getHotel());

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
        ownershipGuard.verifyBookingAccess(booking);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }
        if (booking.getStatus() == BookingStatus.CHECKED_OUT) {
            throw new IllegalStateException("Cannot cancel a checked-out booking");
        }
        // Room availability was already restored for these; cancelling on top would
        // restore it a second time and inflate inventory. Use rebook instead.
        if (booking.getStatus() == BookingStatus.NO_SHOW || booking.getStatus() == BookingStatus.EXPIRED) {
            throw new IllegalStateException(
                    "Cannot cancel a " + booking.getStatus() + " booking");
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
            retainedCommission.setCommissionRate(BigDecimal.valueOf(10.0));
            retainedCommission.setAdminEarnings(commissionRetained);
            retainedCommission.setHotelOwnerEarnings(BigDecimal.ZERO);
            paymentOpt.ifPresent(retainedCommission::setPayment);
            commissionRepository.save(retainedCommission);
        }

        // Credit refund to customer wallet
        if (refundAmount.compareTo(BigDecimal.ZERO) > 0 && booking.getCustomer() != null) {
            walletService.credit(
                    booking.getCustomer().getUser().getId(),
                    refundAmount,
                    "Refund for cancelled booking #" + bookingId + " - " + refundNote,
                    bookingId
            );
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
                customerMsg += " Refund of Ã Â§Â³" + refundAmount + " has been credited to your wallet.";
            }
            sendNotificationToUser(customerUserId, NotificationType.BOOKING_CANCELLED, customerMsg);
            sendNotificationToUser(ownerUserId, NotificationType.BOOKING_CANCELLED,
                    "Booking #" + bookingId + " has been cancelled for " + hotelName + ". Refund note: " + refundNote);
        } catch (Exception ignored) {}

        return BookingMapperDTO.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public BookingResponseDTO rebook(Long oldBookingId, BookingRequestDTO dto) {
        Booking oldBooking = bookingRepository.findById(oldBookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        ownershipGuard.verifyBookingAccess(oldBooking);

        if (oldBooking.getStatus() != BookingStatus.NO_SHOW
                && oldBooking.getStatus() != BookingStatus.EXPIRED
                && oldBooking.getStatus() != BookingStatus.CANCELLED) {
            throw new IllegalStateException("Can only rebook from NO_SHOW, EXPIRED, or CANCELLED bookings");
        }

        return createBooking(dto);
    }

    private String uploadIdImage(MultipartFile file, Long bookingId) {
        // Validated before the try block so a rejection surfaces as its own
        // BadRequestException instead of being rewrapped below.
        String ext = FileUploadUtil.safeExtension(file.getOriginalFilename());
        try {
            Path path = Paths.get(uploadDir, "checkin-id");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String fileName = "booking_" + bookingId + "_" + UUID.randomUUID() + ext;
            Files.copy(file.getInputStream(), path.resolve(fileName));
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("ID image upload failed: " + e.getMessage());
        }
    }

    private void validateDates(Date checkIn, Date checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required");
        }
        if (!checkOut.after(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
    }

    // Validates a coupon for this hotel (active + within date window + under usage limit),
    // increments its redemption count, and returns the discount to subtract from the room total.
    // Returns ZERO when no code is supplied; throws if a supplied code is invalid.
    private BigDecimal applyCoupon(String couponCode, Long hotelId, BigDecimal roomTotal) {
        if (couponCode == null || couponCode.isBlank()) {
            return BigDecimal.ZERO;
        }

        Coupon coupon = couponRepository.findValidByCodeAndHotel_Id(
                couponCode.trim(), hotelId, LocalDateTime.now());
        if (coupon == null) {
            throw new IllegalStateException("Coupon '" + couponCode + "' is invalid, expired, or fully redeemed");
        }

        BigDecimal percentOff = coupon.getDiscountPercent() != null
                ? roomTotal.multiply(BigDecimal.valueOf(coupon.getDiscountPercent()))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal flatOff = coupon.getDiscountAmount() != null
                ? BigDecimal.valueOf(coupon.getDiscountAmount())
                : BigDecimal.ZERO;

        // Never discount more than the room total
        BigDecimal couponDiscount = percentOff.add(flatOff).min(roomTotal);

        // Record the redemption against the usage limit
        coupon.setUsedCount((coupon.getUsedCount() != null ? coupon.getUsedCount() : 0) + 1);
        couponRepository.save(coupon);

        return couponDiscount;
    }

    private void sendNotificationToUser(Long userId, NotificationType type, String message) {
        NotificationRequestDTO dto = new NotificationRequestDTO();
        dto.setUserId(userId);
        dto.setType(type);
        dto.setChannel(NotificationChannel.WEB);
        dto.setMessage(message);
        notificationService.createNotification(dto);
    }

    private void generateInvoiceAtBooking(Booking booking) {
        if (booking.getCustomer() == null) return;

        boolean alreadyExists = invoiceRepository.existsByBooking_Id(booking.getId());
        if (alreadyExists) return;

        Hotel hotel = booking.getHotel();
        PaymentType paymentType = hotel.getPaymentType() != null ? hotel.getPaymentType() : PaymentType.FULL_ADVANCE;

        BigDecimal total = booking.getTotalAmount() != null ? booking.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal discountAmount = booking.getDiscountAmount() != null ? booking.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal taxAmount = booking.getTaxAmount() != null ? booking.getTaxAmount() : BigDecimal.ZERO;
        BigDecimal netAmount = booking.getNetAmount() != null ? booking.getNetAmount() : BigDecimal.ZERO;

        if (paymentType == PaymentType.CASH_ON_STAY) {
            log.info("Skipping invoice at booking for CASH_ON_STAY hotel. Booking: {}", booking.getId());
            return;
        }

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        invoice.setBooking(booking);
        invoice.setCustomer(booking.getCustomer());
        invoice.setStatus(InvoiceStatus.ISSUED);
        invoice.setInvoiceType(InvoiceType.PROFORMA);
        invoice.setIssuedAt(LocalDateTime.now());

        if (paymentType == PaymentType.PARTIAL_ADVANCE) {
            double advancePct = hotel.getAdvancePercentage() != null ? hotel.getAdvancePercentage() : 50.0;
            BigDecimal advanceAmount = netAmount.multiply(BigDecimal.valueOf(advancePct))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            invoice.setTotalAmount(advanceAmount);
            invoice.setDiscountAmount(BigDecimal.ZERO);
            invoice.setTaxAmount(BigDecimal.ZERO);
            invoice.setNetAmount(advanceAmount);
            booking.setAdvanceAmount(advanceAmount);
            booking.setDueAmount(netAmount.subtract(advanceAmount));
            bookingRepository.save(booking);
        } else {
            invoice.setTotalAmount(total);
            invoice.setDiscountAmount(discountAmount);
            invoice.setTaxAmount(taxAmount);
            invoice.setNetAmount(netAmount);
        }

        invoiceRepository.save(invoice);
        log.info("Invoice {} generated at booking time for booking {} (paymentType: {})", invoice.getInvoiceNumber(), booking.getId(), paymentType);

        try {
            auditTrailService.logAction("INVOICE_CREATED", "Invoice", invoice.getId(),
                    "Invoice " + invoice.getInvoiceNumber() + " generated for booking #" + booking.getId() + ", amount: " + invoice.getNetAmount() + ", type: " + paymentType,
                    "SYSTEM");
        } catch (Exception ignored) {}
    }

    // Auto-generates the platform commission when a paid booking completes its stay —
    // previously only an admin manually POSTing to /api/commissions ever created this row,
    // so a normal paid-and-checked-out booking never contributed to earnings reporting.
    private void generateCommissionAtCheckout(Booking booking) {
        if (commissionRepository.existsByBooking_Id(booking.getId())) {
            return;
        }

        Optional<Payment> paymentOpt = paymentRepository.findByBooking_Id(booking.getId());
        if (paymentOpt.isEmpty() || paymentOpt.get().getStatus() != PaymentStatus.PAID) {
            return;
        }
        Payment payment = paymentOpt.get();

        BigDecimal rate = BigDecimal.valueOf(10.0);
        // Base the commission on the net amount actually invoiced/charged (after
        // discount, coupon, tax) so platform + owner earnings reconcile with the
        // invoice total, not the gross room price.
        BigDecimal base = booking.getNetAmount() != null ? booking.getNetAmount()
                : (booking.getTotalAmount() != null ? booking.getTotalAmount()
                        : (booking.getTotalPrice() != null ? booking.getTotalPrice() : BigDecimal.ZERO));
        BigDecimal adminEarnings = base.multiply(rate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal ownerEarnings = base.subtract(adminEarnings);

        Commission commission = new Commission();
        commission.setBooking(booking);
        commission.setPayment(payment);
        commission.setCommissionRate(rate);
        commission.setAdminEarnings(adminEarnings);
        commission.setHotelOwnerEarnings(ownerEarnings);
        commissionRepository.save(commission);
        log.info("Commission auto-generated for booking {}", booking.getId());
    }

    private void generateInvoiceAtCheckout(Booking booking) {
        if (booking.getCustomer() == null) return;

        Hotel hotel = booking.getHotel();
        PaymentType paymentType = hotel.getPaymentType() != null ? hotel.getPaymentType() : PaymentType.FULL_ADVANCE;

        if (paymentType == PaymentType.FULL_ADVANCE) {
            return;
        }

        boolean alreadyExists = invoiceRepository.existsByBooking_Id(booking.getId());
        if (alreadyExists) {
            if (paymentType == PaymentType.CASH_ON_STAY) {
                log.info("Invoice already exists for CASH_ON_STAY booking {}. Skipping.", booking.getId());
                return;
            }
        }

        // PARTIAL_ADVANCE legitimately has an ADVANCE invoice already, so the check
        // above falls through — but a duplicate checkout transition must not create
        // a second FINAL invoice for the same booking.
        if (invoiceRepository.existsByBooking_IdAndInvoiceType(booking.getId(), InvoiceType.FINAL)) {
            log.info("FINAL invoice already exists for booking {}. Skipping.", booking.getId());
            return;
        }

        BigDecimal total = booking.getTotalAmount() != null ? booking.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal discountAmount = booking.getDiscountAmount() != null ? booking.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal taxAmount = booking.getTaxAmount() != null ? booking.getTaxAmount() : BigDecimal.ZERO;
        BigDecimal netAmount = booking.getNetAmount() != null ? booking.getNetAmount() : BigDecimal.ZERO;

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-FINAL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        invoice.setBooking(booking);
        invoice.setCustomer(booking.getCustomer());
        invoice.setStatus(InvoiceStatus.ISSUED);
        invoice.setInvoiceType(InvoiceType.FINAL);
        invoice.setIssuedAt(LocalDateTime.now());

        if (paymentType == PaymentType.PARTIAL_ADVANCE) {
            BigDecimal advanceAmount = booking.getAdvanceAmount() != null ? booking.getAdvanceAmount() : BigDecimal.ZERO;
            BigDecimal remainingAmount = netAmount.subtract(advanceAmount);
            invoice.setTotalAmount(remainingAmount);
            invoice.setDiscountAmount(BigDecimal.ZERO);
            invoice.setTaxAmount(BigDecimal.ZERO);
            invoice.setNetAmount(remainingAmount);
            booking.setDueAmount(BigDecimal.ZERO);
            bookingRepository.save(booking);
        } else {
            invoice.setTotalAmount(total);
            invoice.setDiscountAmount(discountAmount);
            invoice.setTaxAmount(taxAmount);
            invoice.setNetAmount(netAmount);
        }

        invoiceRepository.save(invoice);
        log.info("Checkout invoice {} generated for booking {} (paymentType: {})", invoice.getInvoiceNumber(), booking.getId(), paymentType);

        try {
            auditTrailService.logAction("INVOICE_CREATED", "Invoice", invoice.getId(),
                    "Checkout invoice " + invoice.getInvoiceNumber() + " generated for booking #" + booking.getId() + ", amount: " + invoice.getNetAmount() + ", type: " + paymentType,
                    "SYSTEM");
        } catch (Exception ignored) {}
    }

    @Override
    @Transactional(readOnly = true)
    public List<CheckInCheckOutResponseDTO> getCheckInCheckOutDetails(Long customerId, int page, int size) {
        // Non-admins can only ever see their own upcoming stays — a customer omitting
        // customerId (or passing someone else's) must not get every customer's PII.
        if (!ownershipGuard.isAdmin()) {
            Customer currentCustomer = customerRepository.findByUserEmail(
                            ownershipGuard.getCurrentUser().getEmail())
                    .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
            customerId = currentCustomer.getId();
        }

        List<Booking> bookings;
        if (customerId != null) {
            bookings = bookingRepository.findUpcomingBookingsByCustomer(customerId);
        } else {
            Date now = new Date();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, 30);
            bookings = bookingRepository.findBookingsByCheckInDateBetween(now, cal.getTime());
        }
        return bookings.stream().map(this::mapToCheckInCheckOutDTO).collect(Collectors.toList());
    }

    private CheckInCheckOutResponseDTO mapToCheckInCheckOutDTO(Booking booking) {
        CheckInCheckOutResponseDTO dto = new CheckInCheckOutResponseDTO();
        dto.setBookingId(booking.getId());
        dto.setHotelName(booking.getHotel() != null ? booking.getHotel().getHotelName() : "");
        dto.setRoomType(booking.getRoom() != null ? booking.getRoom().getRoomType() : "");
        dto.setCustomerName(booking.getCustomer() != null ? booking.getCustomer().getCustomerName() : "");
        dto.setCustomerEmail(booking.getCustomer() != null && booking.getCustomer().getUser() != null
                ? booking.getCustomer().getUser().getEmail() : "");
        dto.setCustomerPhone(booking.getCustomer() != null && booking.getCustomer().getPhone() != null
                ? booking.getCustomer().getPhone() : "");
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setBookingStatus(booking.getStatus() != null ? booking.getStatus().name() : "");
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setDueAmount(booking.getDueAmount());
        return dto;
    }}