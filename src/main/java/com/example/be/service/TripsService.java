package com.example.be.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.be.model.Trips;
import com.example.be.repository.TripsRepository;

@Service
public class TripsService {

    private final TripsRepository tripsRepository;

    // Constructor injection
    public TripsService(TripsRepository tripsRepository) {
        this.tripsRepository = tripsRepository;
    }

    public Trips createTrip(Trips trip) {
        trip.setCreatedAt(LocalDateTime.now());
        return tripsRepository.save(trip);
    }

    public List<Trips> getAllTrips() {
        return tripsRepository.findAllNotDeleted();
    }

    public Trips getTripById(Integer tripId) {
        Trips trip = tripsRepository.findByIdNotDeleted(tripId);
        if (trip == null) {
            throw new RuntimeException("Trip not found or has been deleted");
        }
        return trip;
    }

    public Trips updateTrip(Integer tripId, Trips tripDetails) {
        Trips trip = getTripById(tripId);

        trip.setRouteSchedule(tripDetails.getRouteSchedule());
        trip.setDriver(tripDetails.getDriver());
        trip.setAssistant(tripDetails.getAssistant());
        trip.setScheduledDeparture(tripDetails.getScheduledDeparture());
        trip.setScheduledArrival(tripDetails.getScheduledArrival());
        trip.setActualDeparture(tripDetails.getActualDeparture());
        trip.setActualArrival(tripDetails.getActualArrival());
        trip.setTripStatus(tripDetails.getTripStatus());
        trip.setUpdatedAt(LocalDateTime.now());

        return tripsRepository.save(trip);
    }

    public void deleteTrip(Integer tripId) {
        Trips trip = getTripById(tripId);
        trip.markAsDeleted();
        tripsRepository.save(trip);
    }
}