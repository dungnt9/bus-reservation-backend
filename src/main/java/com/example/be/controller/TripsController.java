package com.example.be.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.be.model.Trips;
import com.example.be.service.TripsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/trips")
public class TripsController {

    private final TripsService tripsService;

    public TripsController(TripsService tripsService) {
        this.tripsService = tripsService;
    }

    @GetMapping
    public ResponseEntity<List<Trips>> getAllTrips() {
        return ResponseEntity.ok(tripsService.getAllTrips());
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<Trips> getTripById(@PathVariable Integer tripId) {
        return ResponseEntity.ok(tripsService.getTripById(tripId));
    }

    @PostMapping
    public ResponseEntity<Trips> createTrip(@Valid @RequestBody Trips trip) {
        return ResponseEntity.ok(tripsService.createTrip(trip));
    }

    @PutMapping("/{tripId}")
    public ResponseEntity<Trips> updateTrip(@PathVariable Integer tripId, @Valid @RequestBody Trips trip) {
        return ResponseEntity.ok(tripsService.updateTrip(tripId, trip));
    }

    @DeleteMapping("/{tripId}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Integer tripId) {
        tripsService.deleteTrip(tripId);
        return ResponseEntity.noContent().build();
    }
}
