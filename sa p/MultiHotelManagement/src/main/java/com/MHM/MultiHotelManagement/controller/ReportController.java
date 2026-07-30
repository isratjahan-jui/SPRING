package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.ReportRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.ReportResponseDTO;
import com.MHM.MultiHotelManagement.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

        import java.time.LocalDate;
        import java.util.List;
        import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportResponseDTO> generate(@RequestBody ReportRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.generateReport(dto));
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<ReportResponseDTO>> getByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(reportService.getReportsByHotel(hotelId));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ReportResponseDTO>> getByType(@PathVariable String type) {
        return ResponseEntity.ok(reportService.getReportsByType(type));
    }

    @GetMapping("/platform-summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getPlatformSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getPlatformSummary(startDate, endDate));
    }
}
