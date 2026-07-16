package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.CustomerRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.CustomerResponseDTO;
import com.MHM.MultiHotelManagement.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CustomerController {

    private final CustomerService customerService;

    // ── Create (Register Customer) ──────────────────────────────
    // POST /api/customers
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(
            @RequestPart("data") CustomerRequestDTO dto,
            @RequestPart(value = "image", required = false)
            MultipartFile image
    ) {
        CustomerResponseDTO response =
                customerService.create(dto, image);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // ── Get All ──────────────────────────────────────────────────
    // GET /api/customers
    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAll() {
        return ResponseEntity.ok(customerService.getAll());
    }

    // ── Get by ID ────────────────────────────────────────────────
    // GET /api/customers/1
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(customerService.getById(id));
    }

    // ── Get by User ID ───────────────────────────────────────────
    // GET /api/customers/user/1
    @GetMapping("/user/{userId}")
    public ResponseEntity<CustomerResponseDTO> getByUserId(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                customerService.getByUserId(userId)
        );
    }

    // ── Update ───────────────────────────────────────────────────
    // PUT /api/customers/1
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> update(
            @PathVariable Long id,
            @RequestPart("data") CustomerRequestDTO dto,
            @RequestPart(value = "image", required = false)
            MultipartFile image
    ) {
        return ResponseEntity.ok(
                customerService.update(id, dto, image)
        );
    }

    // ── Delete ───────────────────────────────────────────────────
    // DELETE /api/customers/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}