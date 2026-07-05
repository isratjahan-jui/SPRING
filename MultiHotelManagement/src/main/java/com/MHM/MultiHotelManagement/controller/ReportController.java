package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.ReportRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.ReportResponseDTO;
import com.MHM.MultiHotelManagement.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportResponseDTO> generate(@RequestBody ReportRequestDTO dto) {
        return ResponseEntity.ok(reportService.generateReport(dto));
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<ReportResponseDTO>> getByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(reportService.getReportsByHotel(hotelId));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ReportResponseDTO>> getByType(@PathVariable String type) {
        return ResponseEntity.ok(reportService.getReportsByType(type));
    }
}
