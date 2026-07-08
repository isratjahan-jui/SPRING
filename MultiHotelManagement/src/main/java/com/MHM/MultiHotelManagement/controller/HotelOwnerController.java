package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.HotelOwnerRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.HotelOwnerResponseDTO;
import com.MHM.MultiHotelManagement.service.HotelOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/hotel-owners")
@RequiredArgsConstructor
public class HotelOwnerController {

    private final HotelOwnerService ownerService;

    @PostMapping
    public ResponseEntity<HotelOwnerResponseDTO> create(
            @RequestPart("data") HotelOwnerRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ownerService.createOwner(dto, image));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelOwnerResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ownerService.getOwnerById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<HotelOwnerResponseDTO> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(ownerService.getOwnerByUserId(userId));
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<HotelOwnerResponseDTO> update(
            @PathVariable Long id,
            @RequestPart("data") HotelOwnerRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(ownerService.updateOwner(id, dto, image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        ownerService.deleteOwner(id);
        return ResponseEntity.ok("HotelOwner deleted successfully");
    }

    @GetMapping
    public ResponseEntity<List<HotelOwnerResponseDTO>> getAllOwners() {
        return ResponseEntity.ok(ownerService.getAllOwners());
    }
}
