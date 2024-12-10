package com.example.be.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;  //Đặt tên cột trong bảng cơ sở dữ liệu
import jakarta.persistence.Entity;   //Đánh dấu lớp là 1 thực thể JPA, tương ứng với một bảng trong CSDL.
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;  //Đánh dấu kiểu trường là một Enum.
import jakarta.persistence.GeneratedValue; //Xác định cách khóa chính được tạo. IDENTITY = tự động tạo
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;   //Đánh dấu trường này là khóa chính của bảng
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;   //Định nghĩa bảng trong cơ sở dữ liệu mà thực thể này sẽ ánh xạ
import lombok.AllArgsConstructor;  //Tự động tạo constructor có tham số
import lombok.Data;  //Tự động tạo các phương thức getter, setter, toString(), equals(), và hashCode()
import lombok.NoArgsConstructor;   //Tạo constructor không tham số.
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "drivers")
public class Drivers {
    @Id   //Xác định trường này là khóa chính của bảng
    @GeneratedValue(strategy = GenerationType.IDENTITY)   //Cơ sở dữ liệu sẽ tự động sinh ra giá trị
    @Column(name = "driver_id")
    private Integer driverId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @NotBlank(message = "License number is required")
    @Column(name = "license_number", unique = true, nullable = false, length = 20)
    private String licenseNumber;

    @NotBlank(message = "License class is required")
    @Column(name = "license_class", nullable = false, length = 10)
    private String licenseClass;

    @NotNull(message = "License expiry date is required")
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