package com.MHM.MultiHotelManagement.controller;


import com.MHM.MultiHotelManagement.dto.request.DealsRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.DealsResponseDTO;
import com.MHM.MultiHotelManagement.service.DealsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deals")
@RequiredArgsConstructor
public class DealsController {

    private final DealsService dealsService;

    @PostMapping
    public ResponseEntity<DealsResponseDTO> create(@RequestBody DealsRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dealsService.createDeal(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DealsResponseDTO> update(@PathVariable Long id, @RequestBody DealsRequestDTO dto) {
        return ResponseEntity.ok(dealsService.updateDeal(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<DealsResponseDTO>> getAll() {
        return ResponseEntity.ok(dealsService.getAllActiveDeals());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        dealsService.deleteDeal(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<DealsResponseDTO>> getByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(dealsService.getDealsByHotel(hotelId));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<DealsResponseDTO>> getByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(dealsService.getDealsByRoom(roomId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<DealsResponseDTO>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(dealsService.searchDeals(keyword));
    }
}
