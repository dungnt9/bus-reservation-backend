package com.example.be.repository;

import com.example.be.model.TripSeats;
import com.example.be.model.Trips;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TripsRepository extends JpaRepository<Trips, Integer> {
    @Query("SELECT t FROM Trips t WHERE t.deletedAt IS NULL")
    List<Trips> findAllNotDeleted();

    @Query("SELECT t FROM Trips t " +
            "LEFT JOIN FETCH t.routeSchedule rs " +
            "LEFT JOIN FETCH rs.route r " +
            "WHERE t.tripId = :id AND t.deletedAt IS NULL")
    Trips findTripWithRouteById(Integer id);

    @Query("SELECT t FROM Trips t WHERE t.tripId = :id AND t.deletedAt IS NULL")
    Trips findByIdNotDeleted(Integer id);




    @Query("SELECT t FROM Trips t " +
            "LEFT JOIN FETCH t.routeSchedule rs " +
            "LEFT JOIN FETCH rs.route r " +
            "LEFT JOIN FETCH t.driver d " +
            "LEFT JOIN FETCH t.assistant a " +
            "WHERE t.tripId = :id AND t.deletedAt IS NULL")
    Trips findTripWithDetailsById(@Param("id") Integer id);

    // Modified query to get available trips by route and date with all necessary joins
    @Query("SELECT DISTINCT t FROM Trips t " +
            "LEFT JOIN FETCH t.routeSchedule rs " +
            "LEFT JOIN FETCH rs.route r " +
            "LEFT JOIN FETCH t.driver d " +
            "LEFT JOIN FETCH t.assistant a " +
            "WHERE rs.route.routeId = :routeId " +
            "AND DATE(t.scheduledDeparture) = :departureDate " +
            "AND t.deletedAt IS NULL " +
            "AND r.routeStatus = 'active' " +
            "AND t.tripStatus = 'in_progress'")
    List<Trips> searchAvailableTrips(
            @Param("routeId") Integer routeId,
            @Param("departureDate") LocalDate departureDate
    );

    // Query to check seat availability for a trip
    @Query("SELECT COUNT(ts) FROM TripSeats ts " +
            "WHERE ts.trip.tripId = :tripId " +
            "AND ts.tripSeatStatus = 'available' " +
            "AND ts.deletedAt IS NULL")
    Long countAvailableSeats(@Param("tripId") Integer tripId);

    // Query to find all trip seats with their status
    @Query("SELECT ts FROM TripSeats ts " +
            "LEFT JOIN FETCH ts.vehicleSeat vs " +
            "LEFT JOIN FETCH vs.vehicle v " +
            "WHERE ts.trip.tripId = :tripId " +
            "AND ts.deletedAt IS NULL " +
            "ORDER BY CAST(ts.vehicleSeat.seatNumber AS integer)")
    List<TripSeats> findTripSeatsWithDetails(@Param("tripId") Integer tripId);
}