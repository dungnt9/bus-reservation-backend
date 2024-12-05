package com.example.be.controller;

import com.example.be.model.Invoices;
import com.example.be.service.InvoicesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoicesController {

    @Autowired
    private InvoicesService invoicesService;

    @PostMapping
    public ResponseEntity<Invoices> createInvoice(@Valid @RequestBody Invoices invoice) {
        return ResponseEntity.ok(invoicesService.createInvoice(invoice));
    }

    @GetMapping
    public ResponseEntity<List<Invoices>> getAllInvoices() {
        return ResponseEntity.ok(invoicesService.getAllInvoices());
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<Invoices> getInvoiceById(@PathVariable Integer invoiceId) {
        return ResponseEntity.ok(invoicesService.getInvoiceById(invoiceId));
    }

    @PutMapping("/{invoiceId}")
    public ResponseEntity<Invoices> updateInvoice(@PathVariable Integer invoiceId, @Valid @RequestBody Invoices invoice) {
        return ResponseEntity.ok(invoicesService.updateInvoice(invoiceId, invoice));
    }

    @DeleteMapping("/{invoiceId}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Integer invoiceId) {
        invoicesService.deleteInvoice(invoiceId);
        return ResponseEntity.noContent().build();
    }
}