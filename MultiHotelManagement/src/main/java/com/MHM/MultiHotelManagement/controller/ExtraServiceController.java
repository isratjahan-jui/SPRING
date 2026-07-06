package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.ExtraServiceRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.ExtraServiceResponseDTO;
import com.MHM.MultiHotelManagement.service.ExtraServiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/extra-services")
public class ExtraServiceController {

    private final ExtraServiceService extraServiceService;

    public ExtraServiceController(ExtraServiceService extraServiceService) {
        this.extraServiceService = extraServiceService;
    }

    @PostMapping
    public ResponseEntity<ExtraServiceResponseDTO> createExtraService(@RequestBody ExtraServiceRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(extraServiceService.createExtraService(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExtraServiceResponseDTO> updateExtraService(@PathVariable Long id,
                                                                      @RequestBody ExtraServiceRequestDTO dto) {
        return ResponseEntity.ok(extraServiceService.updateExtraService(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExtraServiceResponseDTO> getExtraServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(extraServiceService.getExtraServiceById(id));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<ExtraServiceResponseDTO>> getExtraServicesByBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(extraServiceService.getExtraServicesByBooking(bookingId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExtraService(@PathVariable Long id) {
        extraServiceService.deleteExtraService(id);
        return ResponseEntity.noContent().build();
    }
}
