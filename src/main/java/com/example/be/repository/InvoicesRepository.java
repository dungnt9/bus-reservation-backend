package com.example.be.repository;

import com.example.be.model.Invoices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoicesRepository extends JpaRepository<Invoices, Integer> {
    // Custom query methods can be added here if needed
}