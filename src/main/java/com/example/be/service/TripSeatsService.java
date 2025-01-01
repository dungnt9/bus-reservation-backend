package com.example.be.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.be.model.TripSeats;
import com.example.be.repository.TripSeatsRepository;

@Service
public class TripSeatsService {

    private final TripSeatsRepository tripSeatsRepository;

    // Constructor injection
    public TripSeatsService(TripSeatsRepository tripSeatsRepository) {
        this.tripSeatsRepository = tripSeatsRepository;
    }

    public TripSeats createTripSeat(TripSeats tripSeat) {
        tripSeat.setCreatedAt(LocalDateTime.now());
        return tripSeatsRepository.save(tripSeat);
    }

    public List<TripSeats> getAllTripSeats() {
        return tripSeatsRepository.findAllNotDeleted();
    }

    public TripSeats getTripSeatById(Integer tripSeatId) {
        TripSeats tripSeat = tripSeatsRepository.findByIdNotDeleted(tripSeatId);
        if (tripSeat == null) {
            throw new RuntimeException("Trip Seat not found or has been deleted");
        }
        return tripSeat;
    }

    public TripSeats updateTripSeat(Integer tripSeatId, TripSeats tripSeatDetails) {
        TripSeats tripSeat = getTripSeatById(tripSeatId);

        tripSeat.setTrip(tripSeatDetails.getTrip());
        tripSeat.setVehicleSeat(tripSeatDetails.getVehicleSeat());
        // Chuyển đổi status từ String sang enum
        if (tripSeatDetails.getTripSeatStatus() != null) {
            TripSeats.TripSeatStatus status = TripSeats.TripSeatStatus.valueOf(tripSeatDetails.getTripSeatStatus().toString());
            tripSeat.setTripSeatStatus(status);
        }
        tripSeat.setUpdatedAt(LocalDateTime.now());

        return tripSeatsRepository.save(tripSeat);
    }
}