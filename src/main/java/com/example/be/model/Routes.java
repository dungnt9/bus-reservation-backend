package com.example.be.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "routes")
public class Routes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Integer routeId;

    @Column(name = "route_name", nullable = false, length = 100)
    private String routeName;

    @Column(name = "ticket_price", nullable = false, precision = 10, scale = 0)
    private BigDecimal ticketPrice;

    @Column(name = "distance", nullable = false, precision = 10, scale = 2)
    private BigDecimal distance;

    @Column(name = "estimated_duration", nullable = false)
    private Integer estimatedDuration;

    @Enumerated(EnumType.STRING)
    @Column(name = "route_status", nullable = false)
    private RouteStatus routeStatus;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public enum RouteStatus {
        active, inactive
    }
}