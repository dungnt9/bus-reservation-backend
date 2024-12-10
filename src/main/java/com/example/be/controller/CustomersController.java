package com.example.be.controller;

import java.util.List; // thao tác với danh sách các phần tử

import org.springframework.http.ResponseEntity; // phản hồi HTTP: Body, Headers, HTTP Status

import org.springframework.web.bind.annotation.GetMapping; // Đánh dấu các phương thức xử lý HTTP request nào
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.PathVariable; // lấy giá trị từ URL để làm tham số method
import org.springframework.web.bind.annotation.RequestBody; // Lấy dữ liệu từ request body vào một object để create/update
import org.springframework.web.bind.annotation.RequestMapping; // Base URL /api/customers cho tất cả các endpoint
import org.springframework.web.bind.annotation.RestController; // nói class này là 1 RESTful Controller

import com.example.be.model.Customers;
import com.example.be.service.CustomersService; // xử lý logic, được gọi trong các method của controller

import jakarta.validation.Valid; // kích hoạt validate annotation validation (@NotNull,...) được định nghĩa trong class

@RestController
@RequestMapping("/api/customers")
public class CustomersController {

    private final CustomersService customersService; // Tiêm dependency bằng Constructor Injection

    // Constructor injection
    public CustomersController(CustomersService customersService) {
        this.customersService = customersService;
    }

    @GetMapping
    public ResponseEntity<List<Customers>> getAllCustomers() {
        return ResponseEntity.ok(customersService.getAllCustomers());
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Customers> getCustomerById(@PathVariable Integer customerId) {
        return ResponseEntity.ok(customersService.getCustomerById(customerId));
    }

    @PostMapping // Xử lý yêu cầu HTTP POST
    public ResponseEntity<Customers> createCustomer(@Valid @RequestBody Customers customer) {
        return ResponseEntity.ok(customersService.createCustomer(customer));
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<Customers> updateCustomer(@PathVariable Integer customerId, @Valid @RequestBody Customers customer) {
        return ResponseEntity.ok(customersService.updateCustomer(customerId, customer));
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer customerId) {
        customersService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build(); // Response với mã 204 No Content (không có dữ liệu trả về)
    }
}
