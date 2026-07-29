package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.LocationRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.LocationResponseDTO;
import com.MHM.MultiHotelManagement.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    // ── Get All Paginated ────────────────────────────────────────
    @GetMapping
    public ResponseEntity<Page<LocationResponseDTO>> getAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(locationService.getAllLocations(pageable));
    }

    // ── Get All (no pagination) ──────────────────────────────────
    @GetMapping("/all")
    public ResponseEntity<List<LocationResponseDTO>> getAll() {
        return ResponseEntity.ok(locationService.getAll());
    }

    // ── Get by ID ────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<LocationResponseDTO> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(locationService.getById(id));
    }

    // ── Get by ID with Hotels ────────────────────────────────────
    @GetMapping("/{id}/hotels")
    public ResponseEntity<LocationResponseDTO> getByIdWithHotels(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                locationService.getByIdWithHotels(id)
        );
    }

    // ── Get by City ──────────────────────────────────────────────
    @GetMapping("/city/{city}")
    public ResponseEntity<List<LocationResponseDTO>> getByCity(
            @PathVariable String city
    ) {
        return ResponseEntity.ok(
                locationService.getByCity(city)
        );
    }

    // ── Search ───────────────────────────────────────────────────
    @GetMapping("/search")
    public ResponseEntity<List<LocationResponseDTO>> search(
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(
                locationService.search(keyword)
        );
    }

    // ── Get Locations with Hotels ────────────────────────────────
    @GetMapping("/with-hotels")
    public ResponseEntity<List<LocationResponseDTO>> getWithHotels() {
        return ResponseEntity.ok(
                locationService.getLocationsWithHotels()
        );
    }

    // ── Update ───────────────────────────────────────────────────
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        locationService.delete(id);
        return ResponseEntity.noContent().build();
    }

}