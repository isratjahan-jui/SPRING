package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.ReviewRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.ReviewResponseDTO;
import com.MHM.MultiHotelManagement.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> create(@RequestBody ReviewRequestDTO dto) {
        return ResponseEntity.ok(reviewService.createReview(dto));
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
        return ResponseEntity.ok(reviewService.getReviewsByHotel(hotelId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok("Review deleted successfully");
    }
}
