package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.LocationRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.LocationResponseDTO;
import com.MHM.MultiHotelManagement.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor

public class LocationController {

    private final LocationService locationService;

    // ── Create ───────────────────────────────────────────────────
    // POST /api/locations
    @PostMapping
    public ResponseEntity<LocationResponseDTO> create(
            @RequestPart("data") LocationRequestDTO dto,
            @RequestPart(value = "image", required = false)
            MultipartFile image
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(locationService.create(dto, image));
    }

    // ── Get All ──────────────────────────────────────────────────
    // GET /api/locations
    @GetMapping
    public ResponseEntity<List<LocationResponseDTO>> getAll() {
        return ResponseEntity.ok(locationService.getAll());
    }

    // ── Get by ID ────────────────────────────────────────────────
    // GET /api/locations/1
    @GetMapping("/{id}")
    public ResponseEntity<LocationResponseDTO> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(locationService.getById(id));
    }

    // ── Get by ID with Hotels ────────────────────────────────────
    // GET /api/locations/1/hotels
    @GetMapping("/{id}/hotels")
    public ResponseEntity<LocationResponseDTO> getByIdWithHotels(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                locationService.getByIdWithHotels(id)
        );
    }

    // ── Get by City ──────────────────────────────────────────────
    // GET /api/locations/city/Dhaka
    @GetMapping("/city/{city}")
    public ResponseEntity<List<LocationResponseDTO>> getByCity(
            @PathVariable String city
    ) {
        return ResponseEntity.ok(
                locationService.getByCity(city)
        );
    }

    // ── Search ───────────────────────────────────────────────────
    // GET /api/locations/search?keyword=dhaka
    @GetMapping("/search")
    public ResponseEntity<List<LocationResponseDTO>> search(
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(
                locationService.search(keyword)
        );
    }

    // ── Get Locations with Hotels ────────────────────────────────
    // GET /api/locations/with-hotels
    @GetMapping("/with-hotels")
    public ResponseEntity<List<LocationResponseDTO>> getWithHotels() {
        return ResponseEntity.ok(
                locationService.getLocationsWithHotels()
        );
    }

    // ── Update ───────────────────────────────────────────────────
    // PUT /api/locations/1
    @PutMapping("/{id}")
    public ResponseEntity<LocationResponseDTO> update(
            @PathVariable Long id,
            @RequestPart("data") LocationRequestDTO dto,
            @RequestPart(value = "image", required = false)
            MultipartFile image
    ) {
        return ResponseEntity.ok(
                locationService.update(id, dto, image)
        );
    }

    // ── Delete ───────────────────────────────────────────────────
    // DELETE /api/locations/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        locationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}