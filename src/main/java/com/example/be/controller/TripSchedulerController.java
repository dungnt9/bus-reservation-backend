package com.example.be.controller;

import com.example.be.service.TripsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.be.service.TripSchedulerService;
import com.example.be.dto.TripDTO;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trip-scheduler")
public class TripSchedulerController {
    private final TripSchedulerService tripSchedulerService;
    private final TripsService tripsService;

    public TripSchedulerController(TripSchedulerService tripSchedulerService, TripsService tripsService) {
        this.tripSchedulerService = tripSchedulerService;
        this.tripsService = tripsService;
    }

    @PostMapping("/test")
    public ResponseEntity<List<TripDTO>> testScheduleTrips(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<TripDTO> createdTrips = tripSchedulerService.createTripsForDateRange(startDate, endDate);
        return ResponseEntity.ok(createdTrips);
    }

    @DeleteMapping("/clean")
    public ResponseEntity<String> deleteTripsInRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(tripSchedulerService.deleteTripsInRange(startDate, endDate));
    }
}