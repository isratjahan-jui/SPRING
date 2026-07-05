package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.ReviewMapperDTO;
import com.MHM.MultiHotelManagement.dto.request.ReviewRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.ReviewResponseDTO;
import com.MHM.MultiHotelManagement.entity.Customer;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.entity.Review;
import com.MHM.MultiHotelManagement.repository.CustomerRepository;
import com.MHM.MultiHotelManagement.repository.HotelRepository;
import com.MHM.MultiHotelManagement.repository.ReviewRepository;
import com.MHM.MultiHotelManagement.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final HotelRepository hotelRepository;
    private final CustomerRepository customerRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             HotelRepository hotelRepository,
                             CustomerRepository customerRepository) {
        this.reviewRepository = reviewRepository;
        this.hotelRepository = hotelRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public ReviewResponseDTO createReview(ReviewRequestDTO dto) {
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found"));
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        // Prevent duplicate review
        if (reviewRepository.existsByCustomer_IdAndHotel_Id(dto.getCustomerId(), dto.getHotelId())) {
            throw new IllegalStateException("Customer already reviewed this hotel");
        }

        Review review = ReviewMapperDTO.toEntity(dto);
        review.setHotel(hotel);
        review.setCustomer(customer);

        Review saved = reviewRepository.save(review);
        return ReviewMapperDTO.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public ReviewResponseDTO updateReview(Long id, ReviewRequestDTO dto) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        Review updated = reviewRepository.save(review);
        return ReviewMapperDTO.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponseDTO getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
        return ReviewMapperDTO.toResponseDTO(review);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByHotel(Long hotelId) {
        return reviewRepository.findByHotelIdWithDetails(hotelId)
                .stream()
                .map(ReviewMapperDTO::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new EntityNotFoundException("Review not found");
        }
        reviewRepository.deleteById(id);
    }
}
