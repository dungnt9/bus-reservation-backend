package com.example.be.repository;

import com.example.be.model.Vehicles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VehiclesRepository extends JpaRepository<Vehicles, Integer> {
    @Query("SELECT v FROM Vehicles v WHERE v.deletedAt IS NULL")
    List<Vehicles> findAllNotDeleted();

    @Query("SELECT v FROM Vehicles v WHERE v.vehicleId = :id AND v.deletedAt IS NULL")
    Vehicles findByIdNotDeleted(Integer id);
}