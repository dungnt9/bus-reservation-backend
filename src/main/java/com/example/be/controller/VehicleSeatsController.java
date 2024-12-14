package com.example.be.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.be.dto.VehicleSeatDTO;
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
    public ResponseEntity<List<VehicleSeatDTO>> getAllVehicleSeats() {
        return ResponseEntity.ok(vehicleSeatsService.getAllVehicleSeats());
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<VehicleSeatDTO>> getVehicleSeatsByVehicleId(@PathVariable Integer vehicleId) {
        return ResponseEntity.ok(vehicleSeatsService.getVehicleSeatsByVehicleId(vehicleId));
    }

    @GetMapping("/{vehicleSeatId}")
    public ResponseEntity<VehicleSeatDTO> getVehicleSeatById(@PathVariable Integer vehicleSeatId) {
        return ResponseEntity.ok(vehicleSeatsService.getVehicleSeatById(vehicleSeatId));
    }

    @PostMapping
    public ResponseEntity<VehicleSeatDTO> createVehicleSeat(@Valid @RequestBody VehicleSeatDTO vehicleSeatDTO) {
        return ResponseEntity.ok(vehicleSeatsService.createVehicleSeat(vehicleSeatDTO));
    }

    @PutMapping("/{vehicleSeatId}")
    public ResponseEntity<VehicleSeatDTO> updateVehicleSeat(
            @PathVariable Integer vehicleSeatId,
            @Valid @RequestBody VehicleSeatDTO vehicleSeatDTO) {
        return ResponseEntity.ok(vehicleSeatsService.updateVehicleSeat(vehicleSeatId, vehicleSeatDTO));
    }

    @DeleteMapping("/{vehicleSeatId}")
    public ResponseEntity<Void> deleteVehicleSeat(@PathVariable Integer vehicleSeatId) {
        vehicleSeatsService.deleteVehicleSeat(vehicleSeatId);
        return ResponseEntity.noContent().build();
    }
}