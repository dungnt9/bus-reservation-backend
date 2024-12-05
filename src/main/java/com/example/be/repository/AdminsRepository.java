package com.example.be.repository;

import com.example.be.model.Admins;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminsRepository extends JpaRepository<Admins, Integer> {
    // Custom query methods can be added here if needed
}