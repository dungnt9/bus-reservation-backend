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
@Table(name = "trip_seats")
public class TripSeats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_seat_id")
    private Integer tripSeatId;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trips trip;

    @ManyToOne
    @JoinColumn(name = "vehicle_seat_id", nullable = false)
    private VehicleSeats vehicleSeat;

    @Enumerated(EnumType.STRING)
    @Column(name = "trip_seat_status", nullable = false)
    private TripSeatStatus tripSeatStatus;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public enum TripSeatStatus {
        available, booked
    }
}