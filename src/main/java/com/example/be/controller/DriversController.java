// DriversController.java
package com.example.be.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.example.be.dto.DriverDTO;
import com.example.be.model.Drivers;
import com.example.be.service.DriversService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/drivers")
public class DriversController {

    private final DriversService driversService;

    public DriversController(DriversService driversService) {
        this.driversService = driversService;
    }

    @GetMapping
    public ResponseEntity<Page<DriverDTO>> getAllDrivers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String dateOfBirth,
            @RequestParam(required = false) String licenseNumber,
            @RequestParam(required = false) String licenseClass,
            @RequestParam(required = false) String licenseExpiry,
            @RequestParam(required = false) String driverStatus
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(driversService.getAllDriversDTO(
                pageable,
                fullName,
                phoneNumber,
                email,
                gender,
                address,
                dateOfBirth,
                licenseNumber,
                licenseClass,
                licenseExpiry,
                driverStatus
        ));
    }

    @GetMapping("/{driverId}")
    public ResponseEntity<DriverDTO> getDriverById(@PathVariable Integer driverId) {
        return ResponseEntity.ok(driversService.getDriverDTOById(driverId));
    }

    @PostMapping
    public ResponseEntity<DriverDTO> createDriver(@Valid @RequestBody Drivers driver) {
        return ResponseEntity.ok(driversService.createDriver(driver));
    }

    @PutMapping("/{driverId}")
    public ResponseEntity<DriverDTO> updateDriver(@PathVariable Integer driverId, @Valid @RequestBody Drivers driver) {
        return ResponseEntity.ok(driversService.updateDriver(driverId, driver));
    }
}