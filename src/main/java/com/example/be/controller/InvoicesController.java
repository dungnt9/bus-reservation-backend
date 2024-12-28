package com.example.be.controller;

import java.util.List;
import java.util.Optional;

import com.example.be.model.Customers;
import com.example.be.repository.CustomersRepository;
import com.example.be.security.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.be.dto.InvoiceDTO;
import com.example.be.dto.CreateInvoiceRequest;
import com.example.be.service.InvoicesService;

@RestController
@RequestMapping("/api/invoices")
public class InvoicesController {

    private final InvoicesService invoicesService;
    private final JwtUtil jwtUtil;
    private final CustomersRepository customersRepository;

    public InvoicesController(
            InvoicesService invoicesService,
            JwtUtil jwtUtil,
            CustomersRepository customersRepository) {
        this.invoicesService = invoicesService;
        this.jwtUtil = jwtUtil;
        this.customersRepository = customersRepository;
    }

    @GetMapping
    public ResponseEntity<Page<InvoiceDTO>> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(invoicesService.getAllInvoicesDTO(pageable));
    }

    @PostMapping("/customer/{userId}")
    public ResponseEntity<InvoiceDTO> createCustomerInvoice(
            @PathVariable Integer userId,
            @RequestBody CreateInvoiceRequest request) {
        try {
            // Tìm customer theo userId
            Optional<Customers> customerOpt = customersRepository.findByUserId(userId);
            if (customerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Tạo invoice với customerId
            return ResponseEntity.ok(invoicesService.createInvoice(
                    customerOpt.get().getCustomerId(),
                    request.getTripId(),
                    request.getSelectedSeats(),
                    request.getPaymentStatus(),
                    request.getPaymentMethod()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
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

    @GetMapping("/customer/{userId}")
    public ResponseEntity<List<InvoiceDTO>> getCustomerInvoices(@PathVariable Integer userId) {
        try {
            // Find customer by userId
            Optional<Customers> customerOpt = customersRepository.findByUserId(userId);
            if (customerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Get customer's invoices
            List<InvoiceDTO> invoices = invoicesService.getCustomerInvoices(customerOpt.get().getCustomerId());
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}