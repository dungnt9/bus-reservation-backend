package com.example.be.controller;

import com.example.be.model.Trips;
import com.example.be.service.TripsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripsController {

    @Autowired
    private TripsService tripsService;

    @PostMapping
    public ResponseEntity<Trips> createTrip(@Valid @RequestBody Trips trip) {
        return ResponseEntity.ok(tripsService.createTrip(trip));
    }

    @GetMapping
    public ResponseEntity<List<Trips>> getAllTrips() {
        return ResponseEntity.ok(tripsService.getAllTrips());
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<Trips> getTripById(@PathVariable Integer tripId) {
        return ResponseEntity.ok(tripsService.getTripById(tripId));
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