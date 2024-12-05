package com.example.be.repository;

import com.example.be.model.Vehicles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehiclesRepository extends JpaRepository<Vehicles, Integer> {
    // Custom query methods can be added here if needed
}