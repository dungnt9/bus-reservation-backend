package com.example.be.service;

import com.example.be.model.Trips;
import com.example.be.repository.TripsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripsService {

    @Autowired
    private TripsRepository tripsRepository;

    public Trips createTrip(Trips trip) {
        return tripsRepository.save(trip);
    }

    public List<Trips> getAllTrips() {
        return tripsRepository.findAll();
    }

    public Trips getTripById(Integer tripId) {
        return tripsRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
    }

    public Trips updateTrip(Integer tripId, Trips tripDetails) {
        Trips trip = getTripById(tripId);
        
        // Update trip details using nested objects
        trip.setRouteSchedule(tripDetails.getRouteSchedule());
        trip.setVehicle(tripDetails.getVehicle());
        trip.setDriver(tripDetails.getDriver());
        trip.setAssistant(tripDetails.getAssistant());
        trip.setScheduledDeparture(tripDetails.getScheduledDeparture());
        trip.setScheduledArrival(tripDetails.getScheduledArrival());
        trip.setActualDeparture(tripDetails.getActualDeparture());
        trip.setActualArrival(tripDetails.getActualArrival());
        trip.setTripStatus(tripDetails.getTripStatus());
    
        return tripsRepository.save(trip);
    }

    public void deleteTrip(Integer tripId) {
        Trips trip = getTripById(tripId);
        tripsRepository.delete(trip);
    }
}