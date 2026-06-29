package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.HotelDetailsRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.HotelDetailsResponseDTO;
import com.MHM.MultiHotelManagement.service.HotelDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/hotel-details")
public class HotelDetailsController {

    private final HotelDetailsService hotelDetailsService;

    public HotelDetailsController(HotelDetailsService hotelDetailsService) {
        this.hotelDetailsService = hotelDetailsService;
    }

    @PostMapping
    public ResponseEntity<HotelDetailsResponseDTO> create(@RequestBody HotelDetailsRequestDTO dto) {
        return ResponseEntity.ok(hotelDetailsService.createHotelDetails(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelDetailsResponseDTO> update(@PathVariable Long id,
                                                          @RequestBody HotelDetailsRequestDTO dto) {
        return ResponseEntity.ok(hotelDetailsService.updateHotelDetails(id, dto));
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<HotelDetailsResponseDTO> getByHotelId(@PathVariable Long hotelId) {
        return ResponseEntity.ok(hotelDetailsService.getHotelDetailsByHotelId(hotelId));
    }


//    @GetMapping("/hotel/{hotelId}")
//    public ResponseEntity<Optional<HotelDetailsResponseDTO>> getByHotelId(@PathVariable Long hotelId) {
//        return ResponseEntity.ok(hotelDetailsService.getHotelDetailsByHotelId(hotelId));
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        hotelDetailsService.deleteHotelDetails(id);
        return ResponseEntity.ok("HotelDetails deleted successfully");
    }
}
