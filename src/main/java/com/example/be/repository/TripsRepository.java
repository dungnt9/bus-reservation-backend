package com.example.be.repository;

import com.example.be.model.Trips;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TripsRepository extends JpaRepository<Trips, Integer> {
    @Query("SELECT t FROM Trips t WHERE t.deletedAt IS NULL")
    List<Trips> findAllNotDeleted();

    @Query("SELECT t FROM Trips t WHERE t.tripId = :id AND t.deletedAt IS NULL")
    Trips findByIdNotDeleted(Integer id);
}