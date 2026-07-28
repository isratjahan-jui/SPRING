package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.FacilityRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.FacilityResponseDTO;
import com.MHM.MultiHotelManagement.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facilities")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    // ── Create একটা Facility ─────────────────────────────────────
    // POST /api/facilities
    @PostMapping
    public ResponseEntity<FacilityResponseDTO> create(
            @RequestBody FacilityRequestDTO dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(facilityService.create(dto));
    }

    // ── Create অনেকগুলো একসাথে ──────────────────────────────────
    // POST /api/facilities/bulk/1
    @PostMapping("/bulk/{hotelId}")
    public ResponseEntity<List<FacilityResponseDTO>> createBulk(
            @PathVariable Long hotelId,
            @RequestBody List<FacilityRequestDTO> dtoList
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(facilityService.createBulk(hotelId, dtoList));
    }

    // ── Get All ──────────────────────────────────────────────────
    // GET /api/facilities
    @GetMapping
    public ResponseEntity<List<FacilityResponseDTO>> getAll() {
        return ResponseEntity.ok(facilityService.getAll());
    }

    // ── Get by ID ────────────────────────────────────────────────
    // GET /api/facilities/1
    @GetMapping("/{id}")
    public ResponseEntity<FacilityResponseDTO> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(facilityService.getById(id));
    }

    // ── Get by Hotel ID ──────────────────────────────────────────
    // GET /api/facilities/hotel/1
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<FacilityResponseDTO>> getByHotelId(
            @PathVariable Long hotelId
    ) {
        return ResponseEntity.ok(
                facilityService.getByHotelId(hotelId)
        );
    }

    // ── Update ───────────────────────────────────────────────────
    // PUT /api/facilities/1
    @PutMapping("/{id}")
    public ResponseEntity<FacilityResponseDTO> update(
            @PathVariable Long id,
            @RequestBody FacilityRequestDTO dto
    ) {
        return ResponseEntity.ok(
                facilityService.update(id, dto)
        );
    }

    // ── Delete একটা ─────────────────────────────────────────────
    // DELETE /api/facilities/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        facilityService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ── Delete Hotel এর সব Facility ─────────────────────────────
    // DELETE /api/facilities/hotel/1
    @DeleteMapping("/hotel/{hotelId}")
    public ResponseEntity<Void> deleteAllByHotelId(
            @PathVariable Long hotelId
    ) {
        facilityService.deleteAllByHotelId(hotelId);
        return ResponseEntity.noContent().build();
    }
}