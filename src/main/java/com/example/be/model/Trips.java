package com.example.be.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "trips")
public class Trips {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id")
    private Integer tripId;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private RouteSchedules routeSchedule;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicles vehicle;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Drivers driver;

    @ManyToOne
    @JoinColumn(name = "assistant_id", nullable = false)
    private Assistants assistant;

    @Column(name = "scheduled_departure", nullable = false)
    private LocalDateTime scheduledDeparture;

    @Column(name = "scheduled_arrival", nullable = false)
    private LocalDateTime scheduledArrival;

    @Column(name = "actual_departure")
    private LocalDateTime actualDeparture;

    @Column(name = "actual_arrival")
    private LocalDateTime actualArrival;

    @Enumerated(EnumType.STRING)
    @Column(name = "trip_status", nullable = false)
    private TripStatus tripStatus;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public enum TripStatus {
        in_progress, completed, cancelled
    }
    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();  // Cập nhật thời gian xóa
    }
}