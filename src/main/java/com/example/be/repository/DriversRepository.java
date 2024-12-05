package com.example.be.repository;

import com.example.be.model.Drivers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriversRepository extends JpaRepository<Drivers, Integer> {
    // Custom query methods can be added here if needed
}