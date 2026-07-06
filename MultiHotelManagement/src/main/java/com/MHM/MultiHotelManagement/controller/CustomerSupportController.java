package com.MHM.MultiHotelManagement.controller;
import com.MHM.MultiHotelManagement.dto.request.CustomerSupportRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.CustomerSupportResponseDTO;
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

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CustomerSupportResponseDTO>> getByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(supportService.getTicketsByCustomer(customerId));
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<CustomerSupportResponseDTO>> getByAgent(@PathVariable Long agentId) {
        return ResponseEntity.ok(supportService.getTicketsByAgent(agentId));
    }
}
