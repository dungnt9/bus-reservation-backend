package com.example.be.repository;

import com.example.be.model.VehicleSeats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VehicleSeatsRepository extends JpaRepository<VehicleSeats, Integer> {
    @Query("SELECT v FROM VehicleSeats v WHERE v.deletedAt IS NULL")
    List<VehicleSeats> findAllNotDeleted();

    @Query("SELECT v FROM VehicleSeats v WHERE v.vehicleSeatId = :id AND v.deletedAt IS NULL")
    VehicleSeats findByIdNotDeleted(Integer id);
}