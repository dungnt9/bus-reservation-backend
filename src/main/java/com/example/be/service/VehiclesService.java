package com.example.be.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.be.model.Vehicles;
import com.example.be.model.VehicleSeats;
import com.example.be.repository.VehiclesRepository;
import com.example.be.repository.VehicleSeatsRepository;

@Service
public class VehiclesService {

    private final VehiclesRepository vehiclesRepository;
    private final VehicleSeatsRepository vehicleSeatsRepository;

    // Constructor injection
    public VehiclesService(
            VehiclesRepository vehiclesRepository,
            VehicleSeatsRepository vehicleSeatsRepository
    ) {
        this.vehiclesRepository = vehiclesRepository;
        this.vehicleSeatsRepository = vehicleSeatsRepository;
    }

    @Transactional
    public Vehicles createVehicle(Vehicles vehicle) {
        // Set creation time
        vehicle.setCreatedAt(LocalDateTime.now());

        // Save the vehicle first
        Vehicles savedVehicle = vehiclesRepository.save(vehicle);

        // Create vehicle seats automatically
        createVehicleSeats(savedVehicle);

        return savedVehicle;
    }

    private void createVehicleSeats(Vehicles vehicle) {
        List<VehicleSeats> seats = new ArrayList<>();

        // Generate seats from 1 to seat capacity
        IntStream.rangeClosed(1, vehicle.getSeatCapacity())
                .forEach(seatNum -> {
                    VehicleSeats seat = new VehicleSeats();
                    seat.setVehicle(vehicle);
                    seat.setSeatNumber(String.valueOf(seatNum));
                    seat.setCreatedAt(LocalDateTime.now());
                    seats.add(seat);
                });

        // Save all seats
        vehicleSeatsRepository.saveAll(seats);
    }

    public List getAllVehicles() {
        return vehiclesRepository.findAllNotDeleted();
    }

    public Vehicles getVehicleById(Integer vehicleId) {
        Vehicles vehicle = vehiclesRepository.findByIdNotDeleted(vehicleId);
        if (vehicle == null) {
            throw new RuntimeException("Vehicle not found or has been deleted");
        }
        return vehicle;
    }

    @Transactional
    public Vehicles updateVehicle(Integer vehicleId, Vehicles vehicleDetails) {
        Vehicles vehicle = getVehicleById(vehicleId);

        // If seat capacity changes, handle seat updates
        if (!vehicle.getSeatCapacity().equals(vehicleDetails.getSeatCapacity())) {
            updateVehicleSeats(vehicle, vehicleDetails.getSeatCapacity());
        }

        vehicle.setPlateNumber(vehicleDetails.getPlateNumber());
        vehicle.setSeatCapacity(vehicleDetails.getSeatCapacity());
        vehicle.setVehicleStatus(vehicleDetails.getVehicleStatus());
        vehicle.setUpdatedAt(LocalDateTime.now());

        return vehiclesRepository.save(vehicle);
    }

    private void updateVehicleSeats(Vehicles vehicle, Integer newSeatCapacity) {
        // Soft delete existing seats
        List<VehicleSeats> existingSeats = vehicleSeatsRepository.findByVehicle(vehicle);
        existingSeats.forEach(seat -> seat.markAsDeleted());
        vehicleSeatsRepository.saveAll(existingSeats);

        // Create new seats if seat capacity increases
        if (newSeatCapacity > vehicle.getSeatCapacity()) {
            List<VehicleSeats> newSeats = new ArrayList<>();
            IntStream.rangeClosed(vehicle.getSeatCapacity() + 1, newSeatCapacity)
                    .forEach(seatNum -> {
                        VehicleSeats seat = new VehicleSeats();
                        seat.setVehicle(vehicle);
                        seat.setSeatNumber(String.valueOf(seatNum));
                        seat.setCreatedAt(LocalDateTime.now());
                        newSeats.add(seat);
                    });

            vehicleSeatsRepository.saveAll(newSeats);
        }
    }

    @Transactional
    public void deleteVehicle(Integer vehicleId) {
        Vehicles vehicle = getVehicleById(vehicleId);

        // Soft delete associated seats
        List<VehicleSeats> seats = vehicleSeatsRepository.findByVehicle(vehicle);
        seats.forEach(seat -> seat.markAsDeleted());
        vehicleSeatsRepository.saveAll(seats);

        // Soft delete the vehicle
        vehicle.markAsDeleted();
        vehiclesRepository.save(vehicle);
    }
}