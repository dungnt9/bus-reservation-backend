package com.example.be.repository;

import com.example.be.model.Drivers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DriversRepository extends JpaRepository<Drivers, Integer> {
    // Tìm tất cả tài xế chưa bị xóa mềm
    @Query("SELECT d FROM Drivers d WHERE d.deletedAt IS NULL")
    List<Drivers> findAllNotDeleted();

    // Tìm tài xế chưa bị xóa mềm theo ID
    @Query("SELECT d FROM Drivers d WHERE d.driverId = :id AND d.deletedAt IS NULL")
    Drivers findByIdNotDeleted(Integer id);
}