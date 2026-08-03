package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.SupportReplyRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.SupportReplyResponseDTO;
import com.MHM.MultiHotelManagement.service.SupportReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support-replies")
@RequiredArgsConstructor
public class SupportReplyController {

    private final SupportReplyService replyService;

    @PostMapping
    public ResponseEntity<SupportReplyResponseDTO> addReply(@RequestBody SupportReplyRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(replyService.addReply(dto));
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<SupportReplyResponseDTO>> getRepliesByTicket(@PathVariable Long ticketId) {
        return ResponseEntity.ok(replyService.getRepliesByTicket(ticketId));
    }
}
