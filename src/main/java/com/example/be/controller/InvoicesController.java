package com.example.be.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.be.model.Invoices;
import com.example.be.dto.InvoiceDTO;
import com.example.be.service.InvoicesService;

import jakarta.validation.Valid;

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
    public ResponseEntity<InvoiceDTO> createInvoice(@Valid @RequestBody Invoices invoice) {
        return ResponseEntity.ok(invoicesService.createInvoice(invoice));
    }

    @PutMapping("/{invoiceId}")
    public ResponseEntity<InvoiceDTO> updateInvoice(@PathVariable Integer invoiceId, @Valid @RequestBody Invoices invoice) {
        return ResponseEntity.ok(invoicesService.updateInvoice(invoiceId, invoice));
    }

    @DeleteMapping("/{invoiceId}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Integer invoiceId) {
        invoicesService.deleteInvoice(invoiceId);
        return ResponseEntity.noContent().build();
    }
}