package com.example.be.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

import com.example.be.model.TripSeats;
import com.example.be.model.Trips;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.be.model.Vehicles;
import com.example.be.model.VehicleSeats;
import com.example.be.repository.VehiclesRepository;
import com.example.be.repository.VehicleSeatsRepository;
import com.example.be.repository.TripsRepository;
import com.example.be.repository.TripSeatsRepository;
import com.example.be.dto.VehicleDropdownDTO;

@Service
public class VehiclesService {

    private final VehiclesRepository vehiclesRepository;
    private final VehicleSeatsRepository vehicleSeatsRepository;
    private final TripsRepository tripsRepository;
    private final TripSeatsRepository tripSeatsRepository;

    public List<VehicleDropdownDTO> getAvailableVehicles() {
        return vehiclesRepository.findAllNotDeleted().stream()
                .filter(vehicle -> vehicle.getVehicleStatus() == Vehicles.VehicleStatus.active)
                .map(this::convertToDropdownDTO)
                .collect(Collectors.toList());
    }

    private VehicleDropdownDTO convertToDropdownDTO(Vehicles vehicle) {
        VehicleDropdownDTO dto = new VehicleDropdownDTO();
        dto.setVehicleId(vehicle.getVehicleId());
        dto.setPlateNumber(vehicle.getPlateNumber());
        dto.setSeatCapacity(vehicle.getSeatCapacity());
        return dto;
    }

    // Constructor injection
    public VehiclesService(
            VehiclesRepository vehiclesRepository,
            VehicleSeatsRepository vehicleSeatsRepository,
            TripsRepository tripsRepository,
            TripSeatsRepository tripSeatsRepository
    ) {
        this.vehiclesRepository = vehiclesRepository;
        this.vehicleSeatsRepository = vehicleSeatsRepository;
        this.tripsRepository = tripsRepository;
        this.tripSeatsRepository = tripSeatsRepository;
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

    public List<VehicleDropdownDTO> getVehiclesForTrip(Integer tripId) {
        // Lấy tất cả xe có status = active
        List<Vehicles> allActiveVehicles = vehiclesRepository.findAllNotDeleted().stream()
                .filter(v -> v.getVehicleStatus() == Vehicles.VehicleStatus.active)
                .collect(Collectors.toList());

        // Lọc: chỉ lấy xe của chuyến hiện tại hoặc xe không trong chuyến nào
        return allActiveVehicles.stream()
                .filter(vehicle ->
                        !isVehicleInAnyTrip(vehicle.getVehicleId()) ||
                                isVehicleInThisTrip(vehicle.getVehicleId(), tripId))
                .map(this::convertToDropdownDTO)
                .collect(Collectors.toList());
    }

    private boolean isVehicleInUse(Vehicles vehicle) {
        // Kiểm tra xem xe có đang được sử dụng trong chuyến xe nào không
        return tripsRepository.findAllNotDeleted().stream()
                .anyMatch(trip -> {
                    List<TripSeats> tripSeats = tripSeatsRepository.findByTripId(trip.getTripId());
                    return !tripSeats.isEmpty() &&
                            tripSeats.get(0).getVehicleSeat().getVehicle().getVehicleId().equals(vehicle.getVehicleId()) &&
                            trip.getTripStatus() == Trips.TripStatus.in_progress;
                });
    }

    private boolean isVehicleUsedInTrip(Vehicles vehicle, Integer tripId) {
        List<TripSeats> tripSeats = tripSeatsRepository.findByTripId(tripId);
        return !tripSeats.isEmpty() &&
                tripSeats.get(0).getVehicleSeat().getVehicle().getVehicleId().equals(vehicle.getVehicleId());
    }

    public List<VehicleDropdownDTO> getAvailableVehiclesNotInTrip() {
        return vehiclesRepository.findAllNotDeleted().stream()
                .filter(vehicle ->
                        vehicle.getVehicleStatus() == Vehicles.VehicleStatus.active &&
                                !isVehicleInAnyTrip(vehicle.getVehicleId()))
                .map(this::convertToDropdownDTO)
                .collect(Collectors.toList());
    }

    private boolean isVehicleInAnyTrip(Integer vehicleId) {
        return tripsRepository.findAllNotDeleted().stream()
                .filter(trip -> trip.getTripStatus() == Trips.TripStatus.in_progress)
                .anyMatch(trip -> {
                    List<TripSeats> tripSeats = tripSeatsRepository.findByTripId(trip.getTripId());
                    return !tripSeats.isEmpty() &&
                            tripSeats.get(0).getVehicleSeat().getVehicle().getVehicleId().equals(vehicleId);
                });
    }

    private boolean isVehicleInThisTrip(Integer vehicleId, Integer tripId) {
        List<TripSeats> tripSeats = tripSeatsRepository.findByTripId(tripId);
        return !tripSeats.isEmpty() &&
                tripSeats.get(0).getVehicleSeat().getVehicle().getVehicleId().equals(vehicleId);
    }
}