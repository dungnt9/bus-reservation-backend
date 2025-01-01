package com.example.be.repository;

import com.example.be.model.Customers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomersRepository extends JpaRepository<Customers, Integer>, JpaSpecificationExecutor<Customers> {

    @Query("SELECT c FROM Customers c WHERE c.customerId = :id AND c.deletedAt IS NULL")
    Customers findByIdNotDeleted(Integer id);

    @Query("SELECT c FROM Customers c WHERE c.user.userId = :userId AND c.deletedAt IS NULL")
    Optional<Customers> findByUserId(@Param("userId") Integer userId);

    @Query("SELECT c FROM Customers c WHERE c.deletedAt IS NULL")
    Page<Customers> findAllNotDeleted(Pageable pageable);
}