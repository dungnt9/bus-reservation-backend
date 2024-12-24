package com.example.be.repository;

import com.example.be.model.Invoices;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InvoicesRepository extends JpaRepository<Invoices, Integer> {
    @Query("SELECT i FROM Invoices i WHERE i.deletedAt IS NULL")
    List<Invoices> findAllNotDeleted();

    @Query("SELECT i FROM Invoices i WHERE i.invoiceId = :id AND i.deletedAt IS NULL")
    Invoices findByIdNotDeleted(Integer id);

    @Query("SELECT i FROM Invoices i " +
            "WHERE i.customer.customerId = :customerId " +
            "AND i.deletedAt IS NULL " +
            "ORDER BY i.invoiceDate DESC")
    List<Invoices> findByCustomerIdNotDeleted(Integer customerId);

    @Query("SELECT i FROM Invoices i WHERE i.deletedAt IS NULL")
    Page<Invoices> findAllNotDeleted(Pageable pageable);
}
