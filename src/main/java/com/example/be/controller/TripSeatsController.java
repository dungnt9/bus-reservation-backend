package com.example.be.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.be.model.TripSeats;
import com.example.be.service.TripSeatsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/trip-seats")
public class TripSeatsController {

    @Autowired
    private TripSeatsService tripSeatsService;

    @PostMapping
    public ResponseEntity<TripSeats> createTripSeat(@Valid @RequestBody TripSeats tripSeat) {
        return ResponseEntity.ok(tripSeatsService.createTripSeat(tripSeat));
    }

    @GetMapping
    public ResponseEntity<List<TripSeats>> getAllTripSeats() {
        return ResponseEntity.ok(tripSeatsService.getAllTripSeats());
    }

    @GetMapping("/{tripSeatId}")
    public ResponseEntity<TripSeats> getTripSeatById(@PathVariable Integer tripSeatId) {
        return ResponseEntity.ok(tripSeatsService.getTripSeatById(tripSeatId));
    }

    @PutMapping("/{tripSeatId}")
    public ResponseEntity<TripSeats> updateTripSeat(@PathVariable Integer tripSeatId, @Valid @RequestBody TripSeats tripSeat) {
        return ResponseEntity.ok(tripSeatsService.updateTripSeat(tripSeatId, tripSeat));
    }

    @DeleteMapping("/{tripSeatId}")
    public ResponseEntity<Void> deleteTripSeat(@PathVariable Integer tripSeatId) {
        tripSeatsService.deleteTripSeat(tripSeatId);
        return ResponseEntity.noContent().build();
    }
}