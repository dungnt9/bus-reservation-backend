package com.example.be.service;

import com.example.be.model.TripSeats;
import com.example.be.repository.TripSeatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripSeatsService {

    @Autowired
    private TripSeatsRepository tripSeatsRepository;

    public TripSeats createTripSeat(TripSeats tripSeat) {
        return tripSeatsRepository.save(tripSeat);
    }

    public List<TripSeats> getAllTripSeats() {
        return tripSeatsRepository.findAll();
    }

    public TripSeats getTripSeatById(Integer tripSeatId) {
        return tripSeatsRepository.findById(tripSeatId)
                .orElseThrow(() -> new RuntimeException("Trip Seat not found"));
    }

    public TripSeats updateTripSeat(Integer tripSeatId, TripSeats tripSeatDetails) {
        TripSeats tripSeat = getTripSeatById(tripSeatId);
        
        tripSeat.setTrip(tripSeatDetails.getTrip());
        tripSeat.setVehicleSeat(tripSeatDetails.getVehicleSeat());
        tripSeat.setTripSeatStatus(tripSeatDetails.getTripSeatStatus());

        return tripSeatsRepository.save(tripSeat);
    }

    public void deleteTripSeat(Integer tripSeatId) {
        TripSeats tripSeat = getTripSeatById(tripSeatId);
        tripSeatsRepository.delete(tripSeat);
    }
}