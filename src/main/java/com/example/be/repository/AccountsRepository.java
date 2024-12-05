package com.example.be.repository;

import com.example.be.model.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts, Integer> {
    // Custom query methods can be added here if needed
}