package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.CustomerSupportRequestDTO;
import com.MHM.MultiHotelManagement.dto.request.EscalationRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.CustomerSupportResponseDTO;
import com.MHM.MultiHotelManagement.dto.response.SupportStatsResponseDTO;
import com.MHM.MultiHotelManagement.service.CustomerSupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class CustomerSupportController {

    private final CustomerSupportService supportService;

    @PostMapping
    public ResponseEntity<CustomerSupportResponseDTO> create(@RequestBody CustomerSupportRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supportService.createTicket(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerSupportResponseDTO> update(@PathVariable Long id, @RequestBody CustomerSupportRequestDTO dto) {
        return ResponseEntity.ok(supportService.updateTicket(id, dto));
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<Void> close(@PathVariable Long id) {
        supportService.closeTicket(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{ticketId}/assign/{agentId}")
    public ResponseEntity<CustomerSupportResponseDTO> assignAgent(
            @PathVariable Long ticketId, @PathVariable Long agentId) {
        return ResponseEntity.ok(supportService.assignAgent(ticketId, agentId));
    }

    @PutMapping("/{ticketId}/escalate")
    public ResponseEntity<CustomerSupportResponseDTO> escalate(
            @PathVariable Long ticketId, @RequestBody(required = false) EscalationRequestDTO dto) {
        if (dto == null) dto = new EscalationRequestDTO();
        return ResponseEntity.ok(supportService.escalateTicket(ticketId, dto));
    }

    @GetMapping
    public ResponseEntity<List<CustomerSupportResponseDTO>> getAll() {
        return ResponseEntity.ok(supportService.getAllTickets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerSupportResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(supportService.getTicketById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CustomerSupportResponseDTO>> getByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(supportService.getTicketsByCustomer(customerId));
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<CustomerSupportResponseDTO>> getByAgent(@PathVariable Long agentId) {
        return ResponseEntity.ok(supportService.getTicketsByAgent(agentId));
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<CustomerSupportResponseDTO>> getByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(supportService.getTicketsByHotel(hotelId));
    }

    @GetMapping("/stats")
    public ResponseEntity<SupportStatsResponseDTO> getStats() {
        return ResponseEntity.ok(supportService.getStats());
    }

    @GetMapping("/stats/hotel/{hotelId}")
    public ResponseEntity<SupportStatsResponseDTO> getStatsByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(supportService.getStatsByHotel(hotelId));
    }
}
