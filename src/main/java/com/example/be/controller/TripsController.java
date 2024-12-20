package com.example.be.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.be.model.Users;
import com.example.be.service.UsersService;
import jakarta.validation.constraints.Pattern;
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

    public TripsController(TripsService tripsService, UsersService usersService) {
        this.tripsService = tripsService;
        this.usersService = usersService;
    }

    @GetMapping
    public ResponseEntity<List<TripDTO>> getAllTrips() {
        return ResponseEntity.ok(tripsService.getAllTrips());
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripDTO> getTripById(@PathVariable Integer tripId) {
        return ResponseEntity.ok(tripsService.getTripById(tripId));
    }

    @GetMapping("/drivers/available")
    public ResponseEntity<List<DriverDropdownDTO>> getAvailableDrivers() {
        return ResponseEntity.ok(tripsService.getAvailableDrivers());
    }

    @GetMapping("/assistants/available")
    public ResponseEntity<List<AssistantDropdownDTO>> getAvailableAssistants() {
        return ResponseEntity.ok(tripsService.getAvailableAssistants());
    }

    @GetMapping("/schedules/active")
    public ResponseEntity<List<RouteScheduleDropdownDTO>> getActiveRouteSchedules() {
        return ResponseEntity.ok(tripsService.getActiveRouteSchedules());
    }

    @GetMapping("/vehicle-seats/available")
    public ResponseEntity<List<VehicleSeatDTO>> getAvailableVehicleSeats() {
        return ResponseEntity.ok(tripsService.getAvailableVehicleSeats());
    }

    @PostMapping
    public ResponseEntity<TripDTO> createTrip(@Valid @RequestBody TripCreateRequest request) {
        return ResponseEntity.ok(tripsService.createTrip(request));
    }

    @PutMapping("/{tripId}")
    public ResponseEntity<TripDTO> updateTrip(
            @PathVariable Integer tripId,
            @Valid @RequestBody TripCreateRequest request) {
        return ResponseEntity.ok(tripsService.updateTrip(tripId, request));
    }

    @DeleteMapping("/{tripId}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Integer tripId) {
        tripsService.deleteTrip(tripId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<TripSearchDTO>> searchTrips(
            @RequestParam Integer routeId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate departureDate
    ) {
        try {
            List<TripSearchDTO> trips = tripsService.searchAvailableTrips(routeId, departureDate);
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
            @Valid @RequestBody TripStatusUpdateRequest request) {
        try {
            TripDTO updatedTrip = tripsService.updateTripStatusAndTimes(tripId, request);
            return ResponseEntity.ok(updatedTrip);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}