package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.BookingRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.BookingResponseDTO;
import com.MHM.MultiHotelManagement.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponseDTO> create(@RequestBody BookingRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> update(@PathVariable Long id,
                                                     @RequestBody BookingRequestDTO dto) {
        return ResponseEntity.ok(bookingService.updateBooking(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> getAll() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<BookingResponseDTO>> getByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(bookingService.getBookingsByCustomer(customerId));
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<BookingResponseDTO>> getByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(bookingService.getBookingsByHotel(hotelId));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<BookingResponseDTO>> getByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(bookingService.getBookingsByRoom(roomId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.ok("Booking deleted successfully");
    }

    // ✅ নতুন অংশ: FoodItem integration

    // বুকিংয়ের সাথে খাবার সিলেক্ট করা
    @PostMapping("/{id}/food-items")
    public ResponseEntity<BookingResponseDTO> addFoodItems(@PathVariable Long id,
                                                           @RequestBody List<Long> foodItemIds) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.addFoodItemsToBooking(id, foodItemIds));
    }

    // বুকিংয়ের খাবার cancel করা
    @PutMapping("/{id}/food-items/cancel")
    public ResponseEntity<BookingResponseDTO> cancelFoodItems(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelFoodItemsFromBooking(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, status));
    }

    @PostMapping(value = "/{id}/online-checkin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookingResponseDTO> onlineCheckIn(
            @PathVariable Long id,
            @RequestPart(value = "idImage", required = false) MultipartFile idImage) {
        return ResponseEntity.ok(bookingService.onlineCheckIn(id, idImage));
    }

    @PostMapping("/{id}/express-checkout")
    public ResponseEntity<BookingResponseDTO> expressCheckOut(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.expressCheckOut(id));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<BookingResponseDTO>> getByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(bookingService.getBookingsByOwner(ownerId));
    }

    @PostMapping("/{id}/no-show")
    public ResponseEntity<BookingResponseDTO> markNoShow(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.markNoShow(id));
    }

    @PatchMapping("/{id}/extra-charges")
    public ResponseEntity<BookingResponseDTO> addExtraCharges(
            @PathVariable Long id,
            @RequestParam double amount) {
        return ResponseEntity.ok(bookingService.addExtraCharges(id, amount));
    }
}