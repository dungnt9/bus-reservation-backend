package com.example.be.repository;

import com.example.be.model.TripSeats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TripSeatsRepository extends JpaRepository<TripSeats, Integer> {
    @Query("SELECT t FROM TripSeats t WHERE t.deletedAt IS NULL")
    List<TripSeats> findAllNotDeleted();

    @Query("SELECT t FROM TripSeats t " +
            "WHERE t.trip.tripId = :tripId " +
            "AND t.vehicleSeat.seatNumber = :seatNumber " +
            "AND t.deletedAt IS NULL")
    TripSeats findByTripAndSeatNumber(Integer tripId, Integer seatNumber);

    @Query("SELECT t FROM TripSeats t WHERE t.tripSeatId = :id AND t.deletedAt IS NULL")
    TripSeats findByIdNotDeleted(Integer id);

    @Query("SELECT t FROM TripSeats t WHERE t.trip.tripId = :tripId AND t.deletedAt IS NULL")
    List<TripSeats> findByTripId(Integer tripId);
}