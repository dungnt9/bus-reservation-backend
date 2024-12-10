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

import com.example.be.model.InvoiceDetails;
import com.example.be.service.InvoiceDetailsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/invoice-details")
public class InvoiceDetailsController {

    private final InvoiceDetailsService invoiceDetailsService;

    public InvoiceDetailsController(InvoiceDetailsService invoiceDetailsService) {
        this.invoiceDetailsService = invoiceDetailsService;
    }

    @GetMapping
    public ResponseEntity<List<InvoiceDetails>> getAllInvoiceDetails() {
        return ResponseEntity.ok(invoiceDetailsService.getAllInvoiceDetails());
    }

    @GetMapping("/{detailId}")
    public ResponseEntity<InvoiceDetails> getInvoiceDetailById(@PathVariable Integer detailId) {
        return ResponseEntity.ok(invoiceDetailsService.getInvoiceDetailById(detailId));
    }

    @PostMapping
    public ResponseEntity<InvoiceDetails> createInvoiceDetail(@Valid @RequestBody InvoiceDetails detail) {
        return ResponseEntity.ok(invoiceDetailsService.createInvoiceDetail(detail));
    }

    @PutMapping("/{detailId}")
    public ResponseEntity<InvoiceDetails> updateInvoiceDetail(@PathVariable Integer detailId, @Valid @RequestBody InvoiceDetails invoiceDetail) {
        return ResponseEntity.ok(invoiceDetailsService.updateInvoiceDetail(detailId, invoiceDetail));
    }

    @DeleteMapping("/{detailId}")
    public ResponseEntity<Void> deleteInvoiceDetail(@PathVariable Integer detailId) {
        invoiceDetailsService.deleteInvoiceDetail(detailId);
        return ResponseEntity.noContent().build();
    }
}
