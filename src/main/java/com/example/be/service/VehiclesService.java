package com.example.be.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.be.model.Vehicles;
import com.example.be.repository.VehiclesRepository;

@Service
public class VehiclesService {

    private final VehiclesRepository vehiclesRepository;

    // Constructor injection
    public VehiclesService(VehiclesRepository vehiclesRepository) {
        this.vehiclesRepository = vehiclesRepository;
    }

    public Vehicles createVehicle(Vehicles vehicle) {
        vehicle.setCreatedAt(LocalDateTime.now());
        return vehiclesRepository.save(vehicle);
    }

    public List<Vehicles> getAllVehicles() {
        return vehiclesRepository.findAllNotDeleted();
    }

    public Vehicles getVehicleById(Integer vehicleId) {
        Vehicles vehicle = vehiclesRepository.findByIdNotDeleted(vehicleId);
        if (vehicle == null) {
            throw new RuntimeException("Vehicle not found or has been deleted");
        }
        return vehicle;
    }

    public Vehicles updateVehicle(Integer vehicleId, Vehicles vehicleDetails) {
        Vehicles vehicle = getVehicleById(vehicleId);

        vehicle.setPlateNumber(vehicleDetails.getPlateNumber());
        vehicle.setSeatCapacity(vehicleDetails.getSeatCapacity());
        vehicle.setVehicleStatus(vehicleDetails.getVehicleStatus());
        vehicle.setUpdatedAt(LocalDateTime.now());

        return vehiclesRepository.save(vehicle);
    }

    public void deleteVehicle(Integer vehicleId) {
        Vehicles vehicle = getVehicleById(vehicleId);
        vehicle.markAsDeleted();
        vehiclesRepository.save(vehicle);
    }
}