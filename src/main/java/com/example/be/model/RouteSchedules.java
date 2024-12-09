package com.example.be.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "route_schedules")
public class RouteSchedules {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Integer scheduleId;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private Routes route;

    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    @Column(name = "is_daily")
    private Boolean isDaily;

    @ElementCollection
    @CollectionTable(name = "route_schedule_days", joinColumns = @JoinColumn(name = "schedule_id"))
    @Column(name = "day_of_week")
    private Set<String> dayOfWeek;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();  // Cập nhật thời gian xóa
    }
}