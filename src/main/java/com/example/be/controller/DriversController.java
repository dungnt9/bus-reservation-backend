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

import com.example.be.model.Drivers;
import com.example.be.service.DriversService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/drivers")
public class DriversController {

    @Autowired
    private DriversService driversService;

    @PostMapping
    public ResponseEntity<Drivers> createDriver(@Valid @RequestBody Drivers driver) {
        return ResponseEntity.ok(driversService.createDriver(driver));
    }

    @GetMapping
    public ResponseEntity<List<Drivers>> getAllDrivers() {
        return ResponseEntity.ok(driversService.getAllDrivers());
    }

    @GetMapping("/{driverId}")
    public ResponseEntity<Drivers> getDriverById(@PathVariable Integer driverId) {
        return ResponseEntity.ok(driversService.getDriverById(driverId));
    }

    @PutMapping("/{driverId}")
    public ResponseEntity<Drivers> updateDriver(@PathVariable Integer driverId, @Valid @RequestBody Drivers driver) {
        return ResponseEntity.ok(driversService.updateDriver(driverId, driver));
    }

    @DeleteMapping("/{driverId}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Integer driverId) {
        driversService.deleteDriver(driverId);
        return ResponseEntity.noContent().build();
    }
}