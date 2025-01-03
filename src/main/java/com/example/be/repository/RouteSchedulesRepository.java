package com.example.be.repository;

import com.example.be.model.RouteSchedules;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RouteSchedulesRepository extends JpaRepository<RouteSchedules, Integer>, JpaSpecificationExecutor<RouteSchedules> {
    @Query("SELECT r FROM RouteSchedules r WHERE r.deletedAt IS NULL")
    List<RouteSchedules> findAllNotDeleted();

    @Query("SELECT r FROM RouteSchedules r WHERE r.deletedAt IS NULL")
    Page<RouteSchedules> findAllNotDeleted(Pageable pageable);

    @Query("SELECT r FROM RouteSchedules r WHERE r.scheduleId = :id AND r.deletedAt IS NULL")
    RouteSchedules findByIdNotDeleted(Integer id);
}