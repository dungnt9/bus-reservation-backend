package com.example.be.repository;

import com.example.be.model.Customers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomersRepository extends JpaRepository<Customers, Integer> {
    // Custom query methods can be added here if needed
}