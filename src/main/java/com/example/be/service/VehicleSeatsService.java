package com.example.be.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.be.model.VehicleSeats;
import com.example.be.repository.VehicleSeatsRepository;

@Service
public class VehicleSeatsService {

    @Autowired
    private VehicleSeatsRepository vehicleSeatsRepository;

    public VehicleSeats createVehicleSeat(VehicleSeats vehicleSeat) {
        return vehicleSeatsRepository.save(vehicleSeat);
    }

    public List<VehicleSeats> getAllVehicleSeats() {
        return vehicleSeatsRepository.findAll();
    }

    public VehicleSeats getVehicleSeatById(Integer vehicleSeatId) {
        return vehicleSeatsRepository.findById(vehicleSeatId)
                .orElseThrow(() -> new RuntimeException("Vehicle Seat not found"));
    }

    public VehicleSeats updateVehicleSeat(Integer vehicleSeatId, VehicleSeats vehicleSeatDetails) {
        VehicleSeats vehicleSeat = getVehicleSeatById(vehicleSeatId);
        
        vehicleSeat.setVehicle(vehicleSeatDetails.getVehicle());
        vehicleSeat.setSeatNumber(vehicleSeatDetails.getSeatNumber());

        return vehicleSeatsRepository.save(vehicleSeat);
    }

    public void deleteVehicleSeat(Integer vehicleSeatId) {
        VehicleSeats vehicleSeat = getVehicleSeatById(vehicleSeatId);
        vehicleSeatsRepository.delete(vehicleSeat);
    }
}