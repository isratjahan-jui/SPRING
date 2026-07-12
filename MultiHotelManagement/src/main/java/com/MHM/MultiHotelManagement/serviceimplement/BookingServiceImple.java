package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.BookingMapperDTO;
import com.MHM.MultiHotelManagement.dto.request.BookingRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.BookingResponseDTO;
import com.MHM.MultiHotelManagement.entity.Booking;
import com.MHM.MultiHotelManagement.entity.Customer;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.entity.Room;
import com.MHM.MultiHotelManagement.entity.FoodItem;
import com.MHM.MultiHotelManagement.enums.BookingStatus;
import com.MHM.MultiHotelManagement.repository.BookingRepository;
import com.MHM.MultiHotelManagement.repository.CustomerRepository;
import com.MHM.MultiHotelManagement.repository.HotelRepository;
import com.MHM.MultiHotelManagement.repository.RoomRepository;
import com.MHM.MultiHotelManagement.repository.FoodItemRepository;
import com.MHM.MultiHotelManagement.service.BookingService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@Service
public class BookingServiceImple implements BookingService {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final FoodItemRepository foodItemRepository;

    @Value("${image.upload.dir:uploads}")
    private String uploadDir;

    public BookingServiceImple(BookingRepository bookingRepository,
                               CustomerRepository customerRepository,
                               HotelRepository hotelRepository,
                               RoomRepository roomRepository,
                               FoodItemRepository foodItemRepository) {
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.foodItemRepository = foodItemRepository;
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

        // Double booking check
        List<Booking> conflicting = bookingRepository.findConflictingBookings(
                room.getId(), dto.getCheckInDate(), dto.getCheckOutDate());
        if (!conflicting.isEmpty()) {
            throw new IllegalStateException("Room is already booked for the selected dates");
        }

        // Calculate totalAmount and dueAmount
        double roomTotal = room.getPricePerNight() * dto.getNumberOfRooms();
        double discount = roomTotal * dto.getDiscountRate() / 100.0;
        double totalAmount = roomTotal - discount;
        booking.setTotalPrice(roomTotal);
        booking.setTotalAmount(totalAmount);
        booking.setDueAmount(totalAmount - dto.getAdvanceAmount());

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

        Booking saved = bookingRepository.save(booking);
        return BookingMapperDTO.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public BookingResponseDTO updateBooking(Long id, BookingRequestDTO dto) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        booking.setCheckInDate(dto.getCheckInDate());
        booking.setCheckOutDate(dto.getCheckOutDate());
        booking.setNumberOfRooms(dto.getNumberOfRooms());
        booking.setTotalGuests(dto.getTotalGuests());
        booking.setDiscountRate(dto.getDiscountRate());
        booking.setAdvanceAmount(dto.getAdvanceAmount());

        // FoodItem integration update
        if (dto.getFoodItemIds() != null) {
            List<FoodItem> foodItems = foodItemRepository.findAllById(dto.getFoodItemIds());
            booking.setFoodItems(foodItems);
        }

        Booking updated = bookingRepository.save(booking);
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
        if (!bookingRepository.existsById(id)) {
            throw new EntityNotFoundException("Booking not found");
        }
        bookingRepository.deleteById(id);
    }

    // ✅ নতুন অংশ: FoodItem integration methods

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
        booking.setStatus(newStatus);
        Booking updated = bookingRepository.save(booking);
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
        Booking updated = bookingRepository.save(booking);
        return BookingMapperDTO.toResponseDTO(updated);
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
        Booking updated = bookingRepository.save(booking);
        return BookingMapperDTO.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public BookingResponseDTO addExtraCharges(Long bookingId, double amount) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        booking.setExtraCharges(booking.getExtraCharges() + amount);
        booking.setDueAmount(booking.getDueAmount() + amount);
        booking.setTotalAmount(booking.getTotalAmount() + amount);
        Booking updated = bookingRepository.save(booking);
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
}
