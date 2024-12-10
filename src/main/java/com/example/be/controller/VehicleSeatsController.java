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

import com.example.be.model.VehicleSeats;
import com.example.be.service.VehicleSeatsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/vehicle-seats")
public class VehicleSeatsController {

    private final VehicleSeatsService vehicleSeatsService;

    public VehicleSeatsController(VehicleSeatsService vehicleSeatsService) {
        this.vehicleSeatsService = vehicleSeatsService;
    }

    @GetMapping
    public ResponseEntity<List<VehicleSeats>> getAllVehicleSeats() {
        return ResponseEntity.ok(vehicleSeatsService.getAllVehicleSeats());
    }

    @GetMapping("/{vehicleSeatId}")
    public ResponseEntity<VehicleSeats> getVehicleSeatById(@PathVariable Integer vehicleSeatId) {
        return ResponseEntity.ok(vehicleSeatsService.getVehicleSeatById(vehicleSeatId));
    }

    @PostMapping
    public ResponseEntity<VehicleSeats> createVehicleSeat(@Valid @RequestBody VehicleSeats vehicleSeat) {
        return ResponseEntity.ok(vehicleSeatsService.createVehicleSeat(vehicleSeat));
    }

    @PutMapping("/{vehicleSeatId}")
    public ResponseEntity<VehicleSeats> updateVehicleSeat(@PathVariable Integer vehicleSeatId, @Valid @RequestBody VehicleSeats vehicleSeat) {
        return ResponseEntity.ok(vehicleSeatsService.updateVehicleSeat(vehicleSeatId, vehicleSeat));
    }

    @DeleteMapping("/{vehicleSeatId}")
    public ResponseEntity<Void> deleteVehicleSeat(@PathVariable Integer vehicleSeatId) {
        vehicleSeatsService.deleteVehicleSeat(vehicleSeatId);
        return ResponseEntity.noContent().build();
    }
}
