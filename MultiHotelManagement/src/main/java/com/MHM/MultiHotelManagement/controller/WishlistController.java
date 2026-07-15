package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.WishlistRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.WishlistResponseDTO;
import com.MHM.MultiHotelManagement.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlists")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    // ── Add to Wishlist ───────────────────────────────
    @PostMapping
    public ResponseEntity<WishlistResponseDTO> add(@RequestBody WishlistRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wishlistService.addToWishlist(dto));
    }

    // ── Remove from Wishlist ──────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        wishlistService.removeFromWishlist(id);
        return ResponseEntity.noContent().build();
    }

    // ── Get All ───────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<WishlistResponseDTO>> getAll() {
        return ResponseEntity.ok(wishlistService.getAll());
    }

    // ── Get by User ───────────────────────────────────
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WishlistResponseDTO>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(wishlistService.getByUserId(userId));
    }

    // ── Get by Customer ───────────────────────────────
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<WishlistResponseDTO>> getByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(wishlistService.getByCustomerId(customerId));
    }

    // ── Get by Hotel ──────────────────────────────────
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<WishlistResponseDTO>> getByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(wishlistService.getByHotelId(hotelId));
    }

    // ── Exists check (Customer + Hotel) ───────────────
    @GetMapping("/exists/customer")
    public ResponseEntity<Boolean> existsByCustomerAndHotel(
            @RequestParam Long customerId,
            @RequestParam Long hotelId
    ) {
        return ResponseEntity.ok(wishlistService.existsByCustomerAndHotel(customerId, hotelId));
    }

    // ── Exists check (User + Hotel) ───────────────────
    @GetMapping("/exists/user")
    public ResponseEntity<Boolean> existsByUserAndHotel(
            @RequestParam Long userId,
            @RequestParam Long hotelId
    ) {
        return ResponseEntity.ok(wishlistService.existsByUserAndHotel(userId, hotelId));
    }
}
