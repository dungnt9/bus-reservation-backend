package com.example.be.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Page<Vehicles>> getAllVehicles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String plateNumber,
            @RequestParam(required = false) String seatCapacity,
            @RequestParam(required = false) String vehicleStatus
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(vehiclesService.getAllVehicles(
                pageable,
                plateNumber,
                seatCapacity,
                vehicleStatus
        ));
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
