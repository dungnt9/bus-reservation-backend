package com.example.be.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.be.dto.VehicleSeatDTO;
import com.example.be.model.VehicleSeats;
import com.example.be.model.Vehicles;
import com.example.be.repository.VehicleSeatsRepository;
import com.example.be.repository.VehiclesRepository;

@Service
public class VehicleSeatsService {

    private final VehicleSeatsRepository vehicleSeatsRepository;
    private final VehiclesRepository vehiclesRepository;

    public VehicleSeatsService(VehicleSeatsRepository vehicleSeatsRepository,
                               VehiclesRepository vehiclesRepository) {
        this.vehicleSeatsRepository = vehicleSeatsRepository;
        this.vehiclesRepository = vehiclesRepository;
    }

    public List<VehicleSeatDTO> getAllVehicleSeats() {
        return vehicleSeatsRepository.findAllNotDeleted().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public VehicleSeatDTO getVehicleSeatById(Integer vehicleSeatId) {
        VehicleSeats seat = vehicleSeatsRepository.findByIdNotDeleted(vehicleSeatId);
        if (seat == null) {
            throw new RuntimeException("Vehicle Seat not found or has been deleted");
        }
        return convertToDTO(seat);
    }

    public List<VehicleSeatDTO> getVehicleSeatsByVehicleId(Integer vehicleId) {
        return vehicleSeatsRepository.findByVehicleIdNotDeleted(vehicleId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public VehicleSeatDTO createVehicleSeat(VehicleSeatDTO dto) {
        Vehicles vehicle = vehiclesRepository.findByIdNotDeleted(dto.getVehicleId());
        if (vehicle == null) {
            throw new RuntimeException("Vehicle not found");
        }

        VehicleSeats seat = new VehicleSeats();
        seat.setVehicle(vehicle);
        seat.setSeatNumber(dto.getSeatNumber());
        seat.setCreatedAt(LocalDateTime.now());

        return convertToDTO(vehicleSeatsRepository.save(seat));
    }

    public VehicleSeatDTO updateVehicleSeat(Integer vehicleSeatId, VehicleSeatDTO dto) {
        VehicleSeats seat = vehicleSeatsRepository.findByIdNotDeleted(vehicleSeatId);
        if (seat == null) {
            throw new RuntimeException("Vehicle Seat not found or has been deleted");
        }

        if (dto.getVehicleId() != null) {
            Vehicles vehicle = vehiclesRepository.findByIdNotDeleted(dto.getVehicleId());
            if (vehicle == null) {
                throw new RuntimeException("Vehicle not found");
            }
            seat.setVehicle(vehicle);
        }

        if (dto.getSeatNumber() != null) {
            seat.setSeatNumber(dto.getSeatNumber());
        }

        seat.setUpdatedAt(LocalDateTime.now());
        return convertToDTO(vehicleSeatsRepository.save(seat));
    }

    public void deleteVehicleSeat(Integer vehicleSeatId) {
        VehicleSeats seat = vehicleSeatsRepository.findByIdNotDeleted(vehicleSeatId);
        if (seat == null) {
            throw new RuntimeException("Vehicle Seat not found or has been deleted");
        }
        seat.markAsDeleted();
        vehicleSeatsRepository.save(seat);
    }

    private VehicleSeatDTO convertToDTO(VehicleSeats seat) {
        VehicleSeatDTO dto = new VehicleSeatDTO();
        dto.setVehicleSeatId(seat.getVehicleSeatId());
        dto.setVehicleId(seat.getVehicle().getVehicleId());
        dto.setPlateNumber(seat.getVehicle().getPlateNumber());
        dto.setSeatNumber(seat.getSeatNumber());
        return dto;
    }
}