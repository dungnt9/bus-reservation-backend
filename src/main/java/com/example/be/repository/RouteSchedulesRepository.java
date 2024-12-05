package com.example.be.repository;

import com.example.be.model.RouteSchedules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteSchedulesRepository extends JpaRepository<RouteSchedules, Integer> {
    // Custom query methods can be added here if needed
}