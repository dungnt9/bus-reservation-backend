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

import com.example.be.model.Vehicles;
import com.example.be.service.VehiclesService;
import com.example.be.dto.VehicleDropdownDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/vehicles")
public class VehiclesController {

    private final VehiclesService vehiclesService;

    public VehiclesController(VehiclesService vehiclesService) {
        this.vehiclesService = vehiclesService;
    }

    @GetMapping
    public ResponseEntity<List<Vehicles>> getAllVehicles() {
        return ResponseEntity.ok(vehiclesService.getAllVehicles());
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<Vehicles> getVehicleById(@PathVariable Integer vehicleId) {
        return ResponseEntity.ok(vehiclesService.getVehicleById(vehicleId));
    }

    @PostMapping
    public ResponseEntity<Vehicles> createVehicle(@Valid @RequestBody Vehicles vehicle) {
        return ResponseEntity.ok(vehiclesService.createVehicle(vehicle));
    }

    @PutMapping("/{vehicleId}")
    public ResponseEntity<Vehicles> updateVehicle(@PathVariable Integer vehicleId, @Valid @RequestBody Vehicles vehicle) {
        return ResponseEntity.ok(vehiclesService.updateVehicle(vehicleId, vehicle));
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Integer vehicleId) {
        vehiclesService.deleteVehicle(vehicleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/available")
    public ResponseEntity<List<VehicleDropdownDTO>> getAvailableVehicles() {
        return ResponseEntity.ok(vehiclesService.getAvailableVehicles());
    }

    @GetMapping("/available-not-in-trip")
    public ResponseEntity<List<VehicleDropdownDTO>> getAvailableVehiclesNotInTrip() {
        return ResponseEntity.ok(vehiclesService.getAvailableVehiclesNotInTrip());
    }

    @GetMapping("/trips/{tripId}/vehicles/available")
    public ResponseEntity<List<VehicleDropdownDTO>> getVehiclesForTrip(@PathVariable Integer tripId) {
        return ResponseEntity.ok(vehiclesService.getVehiclesForTrip(tripId));
    }
}
