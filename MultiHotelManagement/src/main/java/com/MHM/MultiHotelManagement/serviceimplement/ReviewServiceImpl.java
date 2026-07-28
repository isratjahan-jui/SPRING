package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.ReviewMapperDTO;
import com.MHM.MultiHotelManagement.dto.request.NotificationRequestDTO;
import com.MHM.MultiHotelManagement.dto.request.ReviewRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.ReviewResponseDTO;
import com.MHM.MultiHotelManagement.entity.Booking;
import com.MHM.MultiHotelManagement.entity.Customer;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.entity.Review;
import com.MHM.MultiHotelManagement.enums.BookingStatus;
import com.MHM.MultiHotelManagement.enums.NotificationChannel;
import com.MHM.MultiHotelManagement.enums.NotificationType;
import com.MHM.MultiHotelManagement.repository.BookingRepository;
import com.MHM.MultiHotelManagement.repository.CustomerRepository;
import com.MHM.MultiHotelManagement.repository.HotelRepository;
import com.MHM.MultiHotelManagement.repository.ReviewRepository;
import com.MHM.MultiHotelManagement.service.NotificationService;
import com.MHM.MultiHotelManagement.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final HotelRepository hotelRepository;
    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             HotelRepository hotelRepository,
                             CustomerRepository customerRepository,
                             BookingRepository bookingRepository,
                             NotificationService notificationService) {
        this.reviewRepository = reviewRepository;
        this.hotelRepository = hotelRepository;
        this.customerRepository = customerRepository;
        this.bookingRepository = bookingRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public ReviewResponseDTO createReview(ReviewRequestDTO dto) {
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found"));
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        if (dto.getBookingId() == null) {
            throw new IllegalStateException("Booking ID is required to submit a review");
        }

        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        // Verify booking belongs to this customer
        if (booking.getCustomer().getId() != dto.getCustomerId()) {
            throw new IllegalStateException("This booking does not belong to you");
        }

        // Verify booking is for this hotel
        if (booking.getHotel().getId() != dto.getHotelId()) {
            throw new IllegalStateException("This booking is not for this hotel");
        }

        // Only completed bookings can be reviewed
        BookingStatus bookingStatus = booking.getStatus();
        if (bookingStatus != BookingStatus.CHECKED_OUT
                && bookingStatus != BookingStatus.CONFIRMED
                && bookingStatus != BookingStatus.NO_SHOW) {
            throw new IllegalStateException(
                    "You can only review after your stay is completed (current status: " + bookingStatus + ")");
        }

        // Prevent duplicate review per booking
        if (reviewRepository.existsByCustomer_IdAndBooking_Id(dto.getCustomerId(), dto.getBookingId())) {
            throw new IllegalStateException("You have already reviewed this booking");
        }

        Review review = ReviewMapperDTO.toEntity(dto);
        review.setHotel(hotel);
        review.setCustomer(customer);
        review.setBooking(booking);
        review.setStatus("APPROVED");

        Review saved = reviewRepository.save(review);

        // Notify hotel owner
        try {
            NotificationRequestDTO ownerNotification = new NotificationRequestDTO();
            ownerNotification.setUserId(hotel.getOwner().getUser().getId());
            ownerNotification.setType(NotificationType.REVIEW_RECEIVED);
            ownerNotification.setChannel(NotificationChannel.WEB);
            ownerNotification.setMessage("New review received for " + hotel.getHotelName()
                    + " from " + customer.getCustomerName() + ". Rating: " + dto.getRating() + "/5");
            notificationService.createNotification(ownerNotification);
        } catch (Exception ignored) {}

        return ReviewMapperDTO.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public ReviewResponseDTO updateReview(Long id, ReviewRequestDTO dto) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setEditCount(review.getEditCount() + 1);
        review.setEditedAt(LocalDateTime.now());

        Review updated = reviewRepository.save(review);
        return ReviewMapperDTO.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponseDTO getReviewById(Long id) {
        Review review = reviewRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
        return ReviewMapperDTO.toResponseDTO(review);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByHotel(Long hotelId) {
        return reviewRepository.findByHotelIdWithDetailsOrderAll(hotelId)
                .stream()
                .map(ReviewMapperDTO::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getApprovedReviewsByHotel(Long hotelId) {
        return reviewRepository.findByHotelIdWithDetails(hotelId, "APPROVED")
                .stream()
                .map(ReviewMapperDTO::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByCustomer(Long customerId) {
        return reviewRepository.findByCustomerIdWithDetails(customerId)
                .stream()
                .map(ReviewMapperDTO::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getAllReviews() {
        return reviewRepository.findAllWithDetails()
                .stream()
                .map(ReviewMapperDTO::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByStatus(String status) {
        return reviewRepository.findByStatusWithDetails(status)
                .stream()
                .map(ReviewMapperDTO::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReviewResponseDTO approveReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
        review.setStatus("APPROVED");
        Review updated = reviewRepository.save(review);
        return ReviewMapperDTO.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public ReviewResponseDTO rejectReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
        review.setStatus("REJECTED");
        Review updated = reviewRepository.save(review);
        return ReviewMapperDTO.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new EntityNotFoundException("Review not found");
        }
        reviewRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasCustomerReviewedBooking(Long customerId, Long bookingId) {
        return reviewRepository.existsByCustomer_IdAndBooking_Id(customerId, bookingId);
    }
}
