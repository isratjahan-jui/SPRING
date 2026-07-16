package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.HotelExtraServiceRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.HotelExtraServiceResponseDTO;
import com.MHM.MultiHotelManagement.service.HotelExtraServiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotel-extra-services")
public class HotelExtraServiceController {

    private final HotelExtraServiceService service;

    public HotelExtraServiceController(HotelExtraServiceService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<HotelExtraServiceResponseDTO> create(@RequestBody HotelExtraServiceRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelExtraServiceResponseDTO> update(@PathVariable Long id,
                                                                 @RequestBody HotelExtraServiceRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelExtraServiceResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<HotelExtraServiceResponseDTO>> getByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(service.getByHotel(hotelId));
    }

    @GetMapping("/hotel/{hotelId}/active")
    public ResponseEntity<List<HotelExtraServiceResponseDTO>> getActiveByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(service.getActiveByHotel(hotelId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
