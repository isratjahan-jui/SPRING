package com.MHM.MultiHotelManagement.controller;

<<<<<<< Updated upstream
import com.MHM.MultiHotelManagement.dto.request.BookingRoomRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.BookingRoomResponseDTO;
import com.MHM.MultiHotelManagement.service.BookingRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking-rooms")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class BookingRoomController {

    private final BookingRoomService bookingRoomService;

    // ── Create একটা ─────────────────────────────────────────────
    // POST /api/booking-rooms
    @PostMapping
    public ResponseEntity<BookingRoomResponseDTO> create(
            @RequestBody BookingRoomRequestDTO dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookingRoomService.create(dto));
    }

    // ── Create Multiple ──────────────────────────────────────────
    // POST /api/booking-rooms/multiple
    @PostMapping("/multiple")
    public ResponseEntity<List<BookingRoomResponseDTO>> createMultiple(
            @RequestBody List<BookingRoomRequestDTO> dtoList
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookingRoomService.createMultiple(dtoList));
    }

    // ── Get by ID ────────────────────────────────────────────────
    // GET /api/booking-rooms/1
    @GetMapping("/{id}")
    public ResponseEntity<BookingRoomResponseDTO> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                bookingRoomService.getById(id)
        );
    }

    // ── Get by Booking ID ────────────────────────────────────────
    // GET /api/booking-rooms/booking/1
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<BookingRoomResponseDTO>> getByBookingId(
            @PathVariable Long bookingId
    ) {
        return ResponseEntity.ok(
                bookingRoomService.getByBookingId(bookingId)
        );
    }

    // ── Get by Room ID ───────────────────────────────────────────
    // GET /api/booking-rooms/room/1
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<BookingRoomResponseDTO>> getByRoomId(
            @PathVariable Long roomId
    ) {
        return ResponseEntity.ok(
                bookingRoomService.getByRoomId(roomId)
        );
    }

    // ── Get Total Price ──────────────────────────────────────────
    // GET /api/booking-rooms/booking/1/total-price
    @GetMapping("/booking/{bookingId}/total-price")
    public ResponseEntity<Double> getTotalPrice(
            @PathVariable Long bookingId
    ) {
        return ResponseEntity.ok(
                bookingRoomService.getTotalPriceByBookingId(bookingId)
        );
    }

    // ── Update ───────────────────────────────────────────────────
    // PUT /api/booking-rooms/1
    @PutMapping("/{id}")
    public ResponseEntity<BookingRoomResponseDTO> update(
            @PathVariable Long id,
            @RequestBody BookingRoomRequestDTO dto
    ) {
        return ResponseEntity.ok(
                bookingRoomService.update(id, dto)
        );
    }

    // ── Delete একটা ─────────────────────────────────────────────
    // DELETE /api/booking-rooms/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookingRoomService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ── Delete Booking এর সব ────────────────────────────────────
    // DELETE /api/booking-rooms/booking/1
    @DeleteMapping("/booking/{bookingId}")
    public ResponseEntity<Void> deleteAllByBookingId(
            @PathVariable Long bookingId
    ) {
        bookingRoomService.deleteAllByBookingId(bookingId);
        return ResponseEntity.noContent().build();
    }
}
=======
public class BookingRoomController {
}
>>>>>>> Stashed changes
