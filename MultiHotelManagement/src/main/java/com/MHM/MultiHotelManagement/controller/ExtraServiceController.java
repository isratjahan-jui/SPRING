package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.ExtraServiceRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.ExtraServiceResponseDTO;
import com.MHM.MultiHotelManagement.service.ExtraServiceService;
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
    public ExtraServiceResponseDTO createExtraService(@RequestBody ExtraServiceRequestDTO dto) {
        return extraServiceService.createExtraService(dto);
    }

    @PutMapping("/{id}")
    public ExtraServiceResponseDTO updateExtraService(@PathVariable Long id,
                                                      @RequestBody ExtraServiceRequestDTO dto) {
        return extraServiceService.updateExtraService(id, dto);
    }

    @GetMapping("/{id}")
    public ExtraServiceResponseDTO getExtraServiceById(@PathVariable Long id) {
        return extraServiceService.getExtraServiceById(id);
    }

    @GetMapping("/booking/{bookingId}")
    public List<ExtraServiceResponseDTO> getExtraServicesByBooking(@PathVariable Long bookingId) {
        return extraServiceService.getExtraServicesByBooking(bookingId);
    }

    @DeleteMapping("/{id}")
    public void deleteExtraService(@PathVariable Long id) {
        extraServiceService.deleteExtraService(id);
    }
}
