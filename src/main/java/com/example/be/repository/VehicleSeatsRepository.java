package com.example.be.repository;

import com.example.be.model.VehicleSeats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleSeatsRepository extends JpaRepository<VehicleSeats, Integer> {
    // Custom query methods can be added here if needed
}