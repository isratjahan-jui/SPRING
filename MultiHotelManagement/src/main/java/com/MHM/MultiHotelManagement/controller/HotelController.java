package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.HotelRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.HotelResponseDTO;
import com.MHM.MultiHotelManagement.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @PostMapping
    public ResponseEntity<HotelResponseDTO> create(
            @RequestPart("data") HotelRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.createHotel(dto, image));
    }

    @GetMapping("/approved")
    public ResponseEntity<List<HotelResponseDTO>> getApprovedHotels() {
        return ResponseEntity.ok(hotelService.getAllApprovedHotels());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<HotelResponseDTO>> getByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(hotelService.getHotelsByOwner(ownerId));
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<HotelResponseDTO>> getByCity(@PathVariable String city) {
        return ResponseEntity.ok(hotelService.getHotelsByCity(city));
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<HotelResponseDTO> update(
            @PathVariable Long id,
            @RequestPart("data") HotelRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(hotelService.updateHotel(id, dto, image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.ok("Hotel deleted successfully");
    }
}
