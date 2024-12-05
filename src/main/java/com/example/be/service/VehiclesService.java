package com.example.be.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.be.model.Vehicles;
import com.example.be.repository.VehiclesRepository;

@Service
public class VehiclesService {

    @Autowired
    private VehiclesRepository vehiclesRepository;

    public Vehicles createVehicle(Vehicles vehicle) {
        return vehiclesRepository.save(vehicle);
    }

    public List<Vehicles> getAllVehicles() {
        return vehiclesRepository.findAll();
    }

    public Vehicles getVehicleById(Integer vehicleId) {
        return vehiclesRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    public Vehicles updateVehicle(Integer vehicleId, Vehicles vehicleDetails) {
        Vehicles vehicle = getVehicleById(vehicleId);
        
        vehicle.setPlateNumber(vehicleDetails.getPlateNumber());
        vehicle.setSeatCapacity(vehicleDetails.getSeatCapacity());
        vehicle.setVehicleStatus(vehicleDetails.getVehicleStatus());

        return vehiclesRepository.save(vehicle);
    }

    public void deleteVehicle(Integer vehicleId) {
        Vehicles vehicle = getVehicleById(vehicleId);
        vehiclesRepository.delete(vehicle);
    }
}