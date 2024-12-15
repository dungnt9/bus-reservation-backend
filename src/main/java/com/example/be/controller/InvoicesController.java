package com.example.be.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.be.dto.InvoiceDTO;
import com.example.be.dto.CreateInvoiceRequest;
import com.example.be.service.InvoicesService;

@RestController
@RequestMapping("/api/invoices")
public class InvoicesController {

    private final InvoicesService invoicesService;

    public InvoicesController(InvoicesService invoicesService) {
        this.invoicesService = invoicesService;
    }

    @GetMapping
    public ResponseEntity<List<InvoiceDTO>> getAllInvoices() {
        return ResponseEntity.ok(invoicesService.getAllInvoices());
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable Integer invoiceId) {
        return ResponseEntity.ok(invoicesService.getInvoiceById(invoiceId));
    }

    @PostMapping
    public ResponseEntity<InvoiceDTO> createInvoice(@RequestBody CreateInvoiceRequest request) {
        return ResponseEntity.ok(invoicesService.createInvoice(
                request.getCustomerId(),
                request.getTripId(),
                request.getSelectedSeats(),
                request.getPaymentStatus(),
                request.getPaymentMethod()
        ));
    }

    @PutMapping("/{invoiceId}")
    public ResponseEntity<InvoiceDTO> updateInvoice(
            @PathVariable Integer invoiceId,
            @RequestParam String paymentStatus,
            @RequestParam String paymentMethod) {
        return ResponseEntity.ok(invoicesService.updateInvoice(invoiceId, paymentStatus, paymentMethod));
    }

    @DeleteMapping("/{invoiceId}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Integer invoiceId) {
        invoicesService.deleteInvoice(invoiceId);
        return ResponseEntity.noContent().build();
    }
}