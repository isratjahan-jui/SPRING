package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.GalleryRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.GalleryResponseDTO;
import com.MHM.MultiHotelManagement.service.GalleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/gallery")
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryService galleryService;

    // ── Single Image Upload ──────────────────────────────────────
    // POST /api/gallery/upload
    @PostMapping(
            value = "/upload",
            consumes = {"multipart/form-data"}
    )
    public ResponseEntity<GalleryResponseDTO> upload(
            @RequestPart("data") GalleryRequestDTO dto,
            @RequestPart("image") MultipartFile image
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(galleryService.upload(dto, image));
    }

    // ── Multiple Images Upload ───────────────────────────────────
    // POST /api/gallery/upload/multiple/1?category=INTERIOR
    @PostMapping(
            value = "/upload/multiple/{hotelId}",
            consumes = {"multipart/form-data"}
    )
    public ResponseEntity<List<GalleryResponseDTO>> uploadMultiple(
            @PathVariable Long hotelId,
            @RequestParam(defaultValue = "GENERAL") String category,
            @RequestPart("images") List<MultipartFile> images
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(galleryService.uploadMultiple(
                        hotelId, category, images
                ));
    }

    // ── Get All ──────────────────────────────────────────────────
    // GET /api/gallery
    @GetMapping
    public ResponseEntity<List<GalleryResponseDTO>> getAll() {
        return ResponseEntity.ok(galleryService.getAll());
    }

    // ── Get by ID ────────────────────────────────────────────────
    // GET /api/gallery/1
    @GetMapping("/{id}")
    public ResponseEntity<GalleryResponseDTO> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(galleryService.getById(id));
    }

    // ── Get by Hotel ID ──────────────────────────────────────────
    // GET /api/gallery/hotel/1
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<GalleryResponseDTO>> getByHotelId(
            @PathVariable Long hotelId
    ) {
        return ResponseEntity.ok(
                galleryService.getByHotelId(hotelId)
        );
    }

    // ── Get by Hotel ID + Category ───────────────────────────────
    // GET /api/gallery/hotel/1/category/INTERIOR
    @GetMapping("/hotel/{hotelId}/category/{category}")
    public ResponseEntity<List<GalleryResponseDTO>> getByCategory(
            @PathVariable Long hotelId,
            @PathVariable String category
    ) {
        return ResponseEntity.ok(
                galleryService.getByHotelIdAndCategory(
                        hotelId, category
                )
        );
    }

    // ── Update Caption বা Category ───────────────────────────────
    // PUT /api/gallery/1
    @PutMapping("/{id}")
    public ResponseEntity<GalleryResponseDTO> update(
            @PathVariable Long id,
            @RequestBody GalleryRequestDTO dto
    ) {
        return ResponseEntity.ok(
                galleryService.update(id, dto)
        );
    }

    // ── Delete একটা Image ────────────────────────────────────────
    // DELETE /api/gallery/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        galleryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ── Delete Hotel এর সব Images ────────────────────────────────
    // DELETE /api/gallery/hotel/1
    @DeleteMapping("/hotel/{hotelId}")
    public ResponseEntity<Void> deleteAllByHotelId(
            @PathVariable Long hotelId
    ) {
        galleryService.deleteAllByHotelId(hotelId);
        return ResponseEntity.noContent().build();
    }

    // ── Delete Hotel এর নির্দিষ্ট Category ──────────────────────
    // DELETE /api/gallery/hotel/1/category/INTERIOR
    @DeleteMapping("/hotel/{hotelId}/category/{category}")
    public ResponseEntity<Void> deleteByCategory(
            @PathVariable Long hotelId,
            @PathVariable String category
    ) {
        galleryService.deleteByHotelIdAndCategory(
                hotelId, category
        );
        return ResponseEntity.noContent().build();
    }
}