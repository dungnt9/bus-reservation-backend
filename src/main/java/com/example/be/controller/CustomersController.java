// CustomersController.java
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

import com.example.be.dto.CustomerDTO;
import com.example.be.model.Customers;
import com.example.be.service.CustomersService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
public class CustomersController {

    private final CustomersService customersService;

    public CustomersController(CustomersService customersService) {
        this.customersService = customersService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return ResponseEntity.ok(customersService.getAllCustomersDTO());
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Integer customerId) {
        return ResponseEntity.ok(customersService.getCustomerDTOById(customerId));
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody Customers customer) {
        return ResponseEntity.ok(customersService.createCustomer(customer));
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Integer customerId, @Valid @RequestBody Customers customer) {
        return ResponseEntity.ok(customersService.updateCustomer(customerId, customer));
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer customerId) {
        customersService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}