package com.example.be.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.be.model.Users;
import com.example.be.service.UsersService;
import com.example.be.service.VehiclesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.be.dto.*;
import com.example.be.service.TripsService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/trips")
public class TripsController {
    private final TripsService tripsService;
    private final UsersService usersService;
    private final VehiclesService vehiclesService;

    public TripsController(TripsService tripsService, UsersService usersService, VehiclesService vehiclesService) {
        this.tripsService = tripsService;
        this.usersService = usersService;
        this.vehiclesService = vehiclesService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<TripDTO>> getAllTrips() {
        return ResponseEntity.ok(tripsService.getAllTrips());
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripDTO> getTripById(@PathVariable Integer tripId) {
        return ResponseEntity.ok(tripsService.getTripById(tripId));
    }

    @GetMapping
    public ResponseEntity<Page<TripDTO>> getAllTripsWithFilters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer tripId,
            @RequestParam(required = false) String routeName,
            @RequestParam(required = false) String driverName,
            @RequestParam(required = false) String assistantName,
            @RequestParam(required = false) String vehiclePlateNumber,
            @RequestParam(required = false) String tripStatus,
            @RequestParam(required = false) Integer totalSeats,
            @RequestParam(required = false) Integer availableSeats,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledDeparture,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledArrival,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime actualDeparture,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime actualArrival
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(tripsService.getAllTripsWithFilters(
                pageable, tripId, routeName, driverName, assistantName,
                vehiclePlateNumber, tripStatus, totalSeats, availableSeats,
                scheduledDeparture, scheduledArrival, actualDeparture, actualArrival
        ));
    }

    @GetMapping("/drivers/available")
    public ResponseEntity<List<DriverDTO>> getAvailableDrivers() {
        return ResponseEntity.ok(tripsService.getAvailableDrivers());
    }

    @GetMapping("/assistants/available")
    public ResponseEntity<List<AssistantDTO>> getAvailableAssistants() {
        return ResponseEntity.ok(tripsService.getAvailableAssistants());
    }

    @GetMapping("/schedules/active")
    public ResponseEntity<List<RouteScheduleDTO>> getActiveRouteSchedules() {
        return ResponseEntity.ok(tripsService.getActiveRouteSchedules());
    }

    @GetMapping("/vehicle-seats/available")
    public ResponseEntity<List<VehicleSeatDTO>> getAvailableVehicleSeats() {
        return ResponseEntity.ok(tripsService.getAvailableVehicleSeats());
    }

    @PostMapping
    public ResponseEntity<TripDTO> createTrip(@Valid @RequestBody TripDTO request) {
        return ResponseEntity.ok(tripsService.createTrip(request));
    }

    @PutMapping("/{tripId}")
    public ResponseEntity<TripDTO> updateTrip(
            @PathVariable Integer tripId,
            @Valid @RequestBody TripDTO request) {
        return ResponseEntity.ok(tripsService.updateTrip(tripId, request));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TripDTO>> searchTrips(
            @RequestParam Integer routeId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate departureDate
    ) {
        try {
            List<TripDTO> trips = tripsService.searchAvailableTrips(routeId, departureDate);
            return ResponseEntity.ok(trips);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{tripId}/seats")
    public ResponseEntity<TripDetailsDTO> getTripSeats(@PathVariable Integer tripId) {
        try {
            TripDetailsDTO tripDetails = tripsService.getTripDetails(tripId);
            return ResponseEntity.ok(tripDetails);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{tripId}/drivers")
    public ResponseEntity<List<DriverDTO>> getDriversForTrip(@PathVariable Integer tripId) {
        return ResponseEntity.ok(tripsService.getDriverForTrip(tripId));
    }

    @GetMapping("/{tripId}/assistants")
    public ResponseEntity<List<AssistantDTO>> getAssistantsForTrip(@PathVariable Integer tripId) {
        return ResponseEntity.ok(tripsService.getAssistantForTrip(tripId));
    }

    @GetMapping("/{tripId}/vehicles/available")
    public ResponseEntity<List<VehicleDropdownDTO>> getVehiclesForTrip(@PathVariable Integer tripId) {
        try {
            List<VehicleDropdownDTO> vehicles = vehiclesService.getVehiclesForTrip(tripId);
            return ResponseEntity.ok(vehicles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

//    Dành cho tài xế, phụ xe
    @GetMapping("/my-trips/{userId}")
    public ResponseEntity<List<TripDTO>> getMyTrips(@PathVariable Integer userId) {
        try {
            // Get user details to check role
            Users user = usersService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            List<TripDTO> trips = tripsService.getMyTrips(userId, user.getUserRole().toString());
            return ResponseEntity.ok(trips);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{tripId}/status")
    public ResponseEntity<TripDTO> updateTripStatusAndTimes(
            @PathVariable Integer tripId,
            @Valid @RequestBody TripDTO request) {
        try {
            TripDTO updatedTrip = tripsService.updateTripStatusAndTimes(tripId, request);
            return ResponseEntity.ok(updatedTrip);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}