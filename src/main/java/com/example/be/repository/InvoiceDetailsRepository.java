package com.example.be.repository;

import com.example.be.model.InvoiceDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceDetailsRepository extends JpaRepository<InvoiceDetails, Integer> {
    // Custom query methods can be added here if needed
}