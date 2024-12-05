package com.example.be.repository;

import com.example.be.model.Routes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutesRepository extends JpaRepository<Routes, Integer> {
    // Custom query methods can be added here if needed
}