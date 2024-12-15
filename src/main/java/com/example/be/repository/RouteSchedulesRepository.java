package com.example.be.repository;

import com.example.be.model.RouteSchedules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RouteSchedulesRepository extends JpaRepository<RouteSchedules, Integer> {
    @Query("SELECT r FROM RouteSchedules r WHERE r.deletedAt IS NULL")
    List<RouteSchedules> findAllNotDeleted();

    @Query("SELECT r FROM RouteSchedules r WHERE r.scheduleId = :id AND r.deletedAt IS NULL")
    RouteSchedules findByIdNotDeleted(Integer id);

    @Modifying
    @Query("UPDATE RouteSchedules r SET r.deletedAt = :now, r.updatedAt = :now WHERE r.route.routeId = :routeId AND r.deletedAt IS NULL")
    void softDeleteByRouteId(Integer routeId, LocalDateTime now);

    @Query("SELECT DISTINCT r FROM RouteSchedules r " +
            "JOIN FETCH r.route " +
            "WHERE r.deletedAt IS NULL " +
            "AND r.route.routeStatus = 'active' " +
            "AND r.route.deletedAt IS NULL")
    List<RouteSchedules> findActiveSchedulesWithRoute();
}