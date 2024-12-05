package com.example.be.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.be.model.Customers;
import com.example.be.service.CustomersService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
public class CustomersController {

    @Autowired
    private CustomersService customersService;

    @PostMapping
    public ResponseEntity<Customers> createCustomer(@Valid @RequestBody Customers customer) {
        return ResponseEntity.ok(customersService.createCustomer(customer));
    }

    @GetMapping
    public ResponseEntity<List<Customers>> getAllCustomers() {
        return ResponseEntity.ok(customersService.getAllCustomers());
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Customers> getCustomerById(@PathVariable Integer customerId) {
        return ResponseEntity.ok(customersService.getCustomerById(customerId));
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<Customers> updateCustomer(@PathVariable Integer customerId, @Valid @RequestBody Customers customer) {
        return ResponseEntity.ok(customersService.updateCustomer(customerId, customer));
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer customerId) {
        customersService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}