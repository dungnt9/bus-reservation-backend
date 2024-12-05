package com.example.be.repository;

import com.example.be.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    // Custom query methods can be added here if needed
}