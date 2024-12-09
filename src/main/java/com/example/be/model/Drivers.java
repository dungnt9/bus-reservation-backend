package com.example.be.model;

import java.time.LocalDate;
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
@Table(name = "drivers")
public class Drivers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "driver_id")
    private Integer driverId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "license_number", unique = true, nullable = false, length = 20)
    private String licenseNumber;

    @Column(name = "license_class", nullable = false, length = 10)
    private String licenseClass;

    @Column(name = "license_expiry", nullable = false)
    private LocalDate licenseExpiry;

    @Enumerated(EnumType.STRING)
    @Column(name = "driver_status", nullable = false)
    private DriverStatus driverStatus;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public enum DriverStatus {
        available, on_trip, off_duty
    }

    public Integer getUserId() {

        return user != null ? user.getUserId() : null;
    }

    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();  // Cập nhật thời gian xóa
    }
}