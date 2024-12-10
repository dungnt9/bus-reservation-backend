package com.example.be.controller;

import java.util.List; //thao tác với danh sách các phần tử

import org.springframework.http.ResponseEntity;   //phản hồi HTTP: Body, Headers, HTTP Status

import org.springframework.web.bind.annotation.GetMapping;  //Đánh dấu các phương thức xử lý HTTP request nào
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.PathVariable; //lấy giá trị từ URL để làm tham số method
import org.springframework.web.bind.annotation.RequestBody; //Lấy dữ liệu từ request body vào một object để create/ update
import org.springframework.web.bind.annotation.RequestMapping; //Base URL /api/drivers cho tất cả các endpoint
import org.springframework.web.bind.annotation.RestController; //nói class này là 1 RESTful Controller

import com.example.be.model.Drivers;
import com.example.be.service.DriversService; //xử lý logic, được gọi trong các method của controller

import jakarta.validation.Valid; //kích hoạt validate annotation validation (@NotNull,...) được định nghĩa trong class

@RestController
@RequestMapping("/api/drivers")
public class DriversController {

    private final DriversService driversService; //Tiêm dependency bằng Constructor Injection

    // Constructor injection
    public DriversController(DriversService driversService) {
        this.driversService = driversService;
    }

    @GetMapping
    public ResponseEntity<List<Drivers>> getAllDrivers() {
        return ResponseEntity.ok(driversService.getAllDrivers());
    }

    @GetMapping("/{driverId}")
    public ResponseEntity<Drivers> getDriverById(@PathVariable Integer driverId) {
        return ResponseEntity.ok(driversService.getDriverById(driverId));
    }

    @PostMapping   //Xử lý yêu cầu HTTP POST
    public ResponseEntity<Drivers> createDriver(@Valid @RequestBody Drivers driver) {
        return ResponseEntity.ok(driversService.createDriver(driver));
    }

    @PutMapping("/{driverId}")
    public ResponseEntity<Drivers> updateDriver(@PathVariable Integer driverId, @Valid @RequestBody Drivers driver) {
        return ResponseEntity.ok(driversService.updateDriver(driverId, driver));
    }

    @DeleteMapping("/{driverId}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Integer driverId) {
        driversService.deleteDriver(driverId);
        return ResponseEntity.noContent().build();  //Response với mã 204 No Content (không có dữ liệu trả về)
    }
}