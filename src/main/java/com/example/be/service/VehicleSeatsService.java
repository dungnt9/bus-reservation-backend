package com.example.be.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.be.model.VehicleSeats;
import com.example.be.repository.VehicleSeatsRepository;

@Service
public class VehicleSeatsService {

    private final VehicleSeatsRepository vehicleSeatsRepository;

    // Constructor injection
    public VehicleSeatsService(VehicleSeatsRepository vehicleSeatsRepository) {
        this.vehicleSeatsRepository = vehicleSeatsRepository;
    }

    public VehicleSeats createVehicleSeat(VehicleSeats vehicleSeat) {
        vehicleSeat.setCreatedAt(LocalDateTime.now());
        return vehicleSeatsRepository.save(vehicleSeat);
    }

    public List<VehicleSeats> getAllVehicleSeats() {
        return vehicleSeatsRepository.findAllNotDeleted();
    }

    public VehicleSeats getVehicleSeatById(Integer vehicleSeatId) {
        VehicleSeats vehicleSeat = vehicleSeatsRepository.findByIdNotDeleted(vehicleSeatId);
        if (vehicleSeat == null) {
            throw new RuntimeException("Vehicle Seat not found or has been deleted");
        }
        return vehicleSeat;
    }

    public VehicleSeats updateVehicleSeat(Integer vehicleSeatId, VehicleSeats vehicleSeatDetails) {
        VehicleSeats vehicleSeat = getVehicleSeatById(vehicleSeatId);

        vehicleSeat.setVehicle(vehicleSeatDetails.getVehicle());
        vehicleSeat.setSeatNumber(vehicleSeatDetails.getSeatNumber());
        vehicleSeat.setUpdatedAt(LocalDateTime.now());

        return vehicleSeatsRepository.save(vehicleSeat);
    }

    public void deleteVehicleSeat(Integer vehicleSeatId) {
        VehicleSeats vehicleSeat = getVehicleSeatById(vehicleSeatId);
        vehicleSeat.markAsDeleted();
        vehicleSeatsRepository.save(vehicleSeat);
    }
}