package com.example.be.repository;

import com.example.be.model.Trips;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripsRepository extends JpaRepository<Trips, Integer> {
    // Custom query methods can be added here if needed
}