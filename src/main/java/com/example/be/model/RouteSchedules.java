package com.example.be.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

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

    @Column(name = "day_of_week") // Đây là trường SET trong MySQL
    private String dayOfWeek; // Lưu trữ dưới dạng chuỗi trong Java

    // Method để convert string từ database sang List<String>
    public List<String> getDaysOfWeek() {
        if (dayOfWeek != null && !dayOfWeek.isEmpty()) {
            return Arrays.stream(dayOfWeek.split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)  // Convert to uppercase
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    // Method để save List<String> vào database
    public void setDaysOfWeek(List<String> days) {
        if (days != null && !days.isEmpty()) {
            this.dayOfWeek = days.stream()
                    .map(String::toUpperCase)  // Ensure uppercase when saving
                    .collect(Collectors.joining(","));
        } else {
            this.dayOfWeek = null;
        }
    }

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