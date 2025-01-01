package com.example.be.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.be.model.TripSeats;
import com.example.be.service.TripSeatsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/trip-seats")
public class TripSeatsController {

    private final TripSeatsService tripSeatsService;

    public TripSeatsController(TripSeatsService tripSeatsService) {
        this.tripSeatsService = tripSeatsService;
    }

    @GetMapping
    public ResponseEntity<List<TripSeats>> getAllTripSeats() {
        return ResponseEntity.ok(tripSeatsService.getAllTripSeats());
    }

    @GetMapping("/{tripSeatId}")
    public ResponseEntity<TripSeats> getTripSeatById(@PathVariable Integer tripSeatId) {
        return ResponseEntity.ok(tripSeatsService.getTripSeatById(tripSeatId));
    }

    @PostMapping
    public ResponseEntity<TripSeats> createTripSeat(@Valid @RequestBody TripSeats tripSeat) {
        return ResponseEntity.ok(tripSeatsService.createTripSeat(tripSeat));
    }

    @PutMapping("/{tripSeatId}")
    public ResponseEntity<TripSeats> updateTripSeat(@PathVariable Integer tripSeatId, @Valid @RequestBody TripSeats tripSeat) {
        return ResponseEntity.ok(tripSeatsService.updateTripSeat(tripSeatId, tripSeat));
    }
}
