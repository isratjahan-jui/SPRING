package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.ReviewRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.ReviewResponseDTO;
import com.MHM.MultiHotelManagement.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> create(@RequestBody ReviewRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> update(@PathVariable Long id,
                                                    @RequestBody ReviewRequestDTO dto) {
        return ResponseEntity.ok(reviewService.updateReview(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<ReviewResponseDTO>> getByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(reviewService.getApprovedReviewsByHotel(hotelId));
    }

    @GetMapping("/hotel/{hotelId}/all")
    public ResponseEntity<List<ReviewResponseDTO>> getByHotelAll(@PathVariable Long hotelId) {
        return ResponseEntity.ok(reviewService.getReviewsByHotel(hotelId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ReviewResponseDTO>> getByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(reviewService.getReviewsByCustomer(customerId));
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkReview(
            @RequestParam Long customerId,
            @RequestParam Long bookingId) {
        return ResponseEntity.ok(Map.of(
                "reviewed", reviewService.hasCustomerReviewedBooking(customerId, bookingId)));
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponseDTO>> getAll() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ReviewResponseDTO>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(reviewService.getReviewsByStatus(status));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ReviewResponseDTO> approve(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.approveReview(id));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ReviewResponseDTO> reject(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.rejectReview(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok("Review deleted successfully");
    }
}
