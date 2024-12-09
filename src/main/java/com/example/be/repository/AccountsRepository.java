package com.example.be.repository;

import com.example.be.model.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts, Integer> {
    @Query("SELECT a FROM Accounts a WHERE a.deletedAt IS NULL")
    List<Accounts> findAllNotDeleted();

    @Query("SELECT a FROM Accounts a WHERE a.accountId = :id AND a.deletedAt IS NULL")
    Accounts findByIdNotDeleted(Integer id);
}