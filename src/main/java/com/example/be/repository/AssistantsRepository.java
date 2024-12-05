package com.example.be.repository;

import com.example.be.model.Assistants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssistantsRepository extends JpaRepository<Assistants, Integer> {
    // Custom query methods can be added here if needed
}