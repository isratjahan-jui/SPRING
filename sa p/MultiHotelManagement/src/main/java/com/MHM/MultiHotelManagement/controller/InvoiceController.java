package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.InvoiceRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.InvoiceResponseDTO;
import com.MHM.MultiHotelManagement.entity.Invoice;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.InvoiceRepository;
import com.MHM.MultiHotelManagement.service.InvoiceService;
import com.MHM.MultiHotelManagement.serviceimplement.InvoicePdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final InvoicePdfService invoicePdfService;
    private final InvoiceRepository invoiceRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HOTEL_OWNER')")
    public ResponseEntity<InvoiceResponseDTO> create(@RequestBody InvoiceRequestDTO dto) {
        return ResponseEntity.status(201).body(invoiceService.create(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'HOTEL_OWNER')")
    public ResponseEntity<InvoiceResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getById(id));
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'HOTEL_OWNER')")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));

        byte[] pdfBytes = invoicePdfService.generateInvoicePdf(invoice);

        String fileName = "Invoice_" + (invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber() : id) + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(pdfBytes);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<List<InvoiceResponseDTO>> getByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(invoiceService.getByCustomer(customerId));
    }

    @GetMapping("/booking/{bookingId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'HOTEL_OWNER')")
    public ResponseEntity<List<InvoiceResponseDTO>> getByBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(invoiceService.getByBooking(bookingId));
    }

    @GetMapping("/hotel/{hotelId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOTEL_OWNER')")
    public ResponseEntity<List<InvoiceResponseDTO>> getByHotelId(@PathVariable Long hotelId) {
        return ResponseEntity.ok(invoiceService.getByHotelId(hotelId));
    }

    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOTEL_OWNER')")
    public ResponseEntity<List<InvoiceResponseDTO>> getByOwnerId(@PathVariable Long ownerId) {
        return ResponseEntity.ok(invoiceService.getByOwnerId(ownerId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InvoiceResponseDTO>> getAll() {
        return ResponseEntity.ok(invoiceService.getAll());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        invoiceService.delete(id);
        return ResponseEntity.ok("Invoice deleted successfully");
    }
}
