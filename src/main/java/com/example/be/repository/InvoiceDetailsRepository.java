package com.example.be.repository;

import com.example.be.model.InvoiceDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InvoiceDetailsRepository extends JpaRepository<InvoiceDetails, Integer> {
    @Query("SELECT i FROM InvoiceDetails i WHERE i.deletedAt IS NULL")
    List<InvoiceDetails> findAllNotDeleted();

    @Query("SELECT i FROM InvoiceDetails i WHERE i.detailId = :id AND i.deletedAt IS NULL")
    InvoiceDetails findByIdNotDeleted(Integer id);

    @Query("SELECT d FROM InvoiceDetails d WHERE d.invoice.invoiceId = :invoiceId AND d.deletedAt IS NULL")
    List<InvoiceDetails> findByInvoiceId(Integer invoiceId);
}