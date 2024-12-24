package com.example.be.repository;

import com.example.be.model.Assistants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssistantsRepository extends JpaRepository<Assistants, Integer> {
    @Query("SELECT a FROM Assistants a WHERE a.deletedAt IS NULL")
    List<Assistants> findAllNotDeleted();

    @Query("SELECT a FROM Assistants a WHERE a.assistantId = :id AND a.deletedAt IS NULL")
    Assistants findByIdNotDeleted(Integer id);

    @Query("SELECT a FROM Assistants a WHERE a.deletedAt IS NULL")
    Page<Assistants> findAllNotDeleted(Pageable pageable);
}