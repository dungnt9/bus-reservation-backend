package com.example.be.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
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

    @Column(name = "day_of_week") // Đây là trường SET trong MySQL
    private String dayOfWeek; // Lưu trữ dưới dạng chuỗi trong Java

    // Chuyển đổi từ chuỗi (lưu trong MySQL) thành danh sách các ngày
    public List<String> getDaysOfWeek() {
        if (dayOfWeek != null && !dayOfWeek.isEmpty()) {
            return Arrays.asList(dayOfWeek.split(","));
        }
        return Arrays.asList();  // Trả về danh sách trống nếu dayOfWeek là null hoặc rỗng
    }

    // Chuyển đổi danh sách các ngày thành chuỗi để lưu vào MySQL
    public void setDaysOfWeek(List<String> days) {
        if (days != null && !days.isEmpty()) {
            this.dayOfWeek = String.join(",", days);  // Nối các ngày thành chuỗi ngăn cách bằng dấu phẩy
        } else {
            this.dayOfWeek = null;  // Nếu danh sách trống, lưu null
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