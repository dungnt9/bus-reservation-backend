package com.example.be.repository;

import com.example.be.model.Customers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomersRepository extends JpaRepository<Customers, Integer> {
    @Query("SELECT c FROM Customers c WHERE c.deletedAt IS NULL")
    List<Customers> findAllNotDeleted();

    @Query("SELECT c FROM Customers c WHERE c.customerId = :id AND c.deletedAt IS NULL")
    Customers findByIdNotDeleted(Integer id);
}