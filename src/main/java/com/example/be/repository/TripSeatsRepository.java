package com.example.be.repository;

import com.example.be.model.TripSeats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripSeatsRepository extends JpaRepository<TripSeats, Integer> {
    // Custom query methods can be added here if needed
}