-- Create and use database
CREATE DATABASE db;
USE db;

-- Create Users table with simplified structure
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name NVARCHAR(100) NOT NULL,
    phone_number VARCHAR(10) NOT NULL,
    email VARCHAR(100),
    password_hash VARCHAR(50),
    gender ENUM('male', 'female', 'other'),
    address NVARCHAR(255),
    date_of_birth DATE,
    user_role ENUM('admin', 'driver', 'assistant', 'customer') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    INDEX idx_deleted_at (deleted_at)
);

-- Create Admins table
CREATE TABLE admins (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    INDEX idx_deleted_at (deleted_at),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    INDEX idx_user_id (user_id)
);

-- Create Customers table
CREATE TABLE customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    INDEX idx_deleted_at (deleted_at),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    INDEX idx_user_id (user_id)
);

-- Create Drivers table
CREATE TABLE drivers (
    driver_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    license_number VARCHAR(20) UNIQUE NOT NULL,
    license_class VARCHAR(10) NOT NULL,
    license_expiry DATE NOT NULL,
    driver_status ENUM('available', 'on_trip', 'off_duty') DEFAULT 'available',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    INDEX idx_deleted_at (deleted_at),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    INDEX idx_user_id (user_id)
);

-- Create Assistants table
CREATE TABLE assistants (
    assistant_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    assistant_status ENUM('available', 'on_trip', 'off_duty') DEFAULT 'available',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    INDEX idx_deleted_at (deleted_at),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    INDEX idx_user_id (user_id)
);

-- Create Vehicles table
CREATE TABLE vehicles (
    vehicle_id INT PRIMARY KEY AUTO_INCREMENT,
    plate_number VARCHAR(20) UNIQUE NOT NULL,
    seat_capacity INT NOT NULL,
    vehicle_status ENUM('active', 'maintenance', 'retired') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    INDEX idx_deleted_at (deleted_at)
);

-- Create Vehicle_Seats table (NEW)
CREATE TABLE vehicle_seats (
    vehicle_seat_id INT PRIMARY KEY AUTO_INCREMENT,
    vehicle_id INT NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    INDEX idx_deleted_at (deleted_at),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id),
    INDEX idx_vehicle_id (vehicle_id),
    UNIQUE KEY unique_vehicle_seat (vehicle_id, seat_number)
);

-- Create Routes table
CREATE TABLE routes (
    route_id INT PRIMARY KEY AUTO_INCREMENT,
    route_name NVARCHAR(100) NOT NULL,
    ticket_price DECIMAL(10,0) NOT NULL,
    distance DECIMAL(10,2) NOT NULL,
    estimated_duration INT NOT NULL,
    route_status ENUM('active', 'inactive') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    INDEX idx_deleted_at (deleted_at)
);

-- Create Route Schedules table
-- Một lịch tuyến xe có thể tương ứng với nhiều chuyến xe
-- Lịch tuyến xe là cụ thể cho tuyến xe về giờ, ngày nào trong tuần sẽ mở tuyến 
CREATE TABLE route_schedules (
    schedule_id INT PRIMARY KEY AUTO_INCREMENT,
    route_id INT NOT NULL,
    departure_time TIME NOT NULL,
    day_of_week SET('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    INDEX idx_deleted_at (deleted_at),
    FOREIGN KEY (route_id) REFERENCES routes(route_id),
    INDEX idx_route_id (route_id)
);

-- Create Trips table
-- Chuyến xe muốn biết dùng xe nào thì sẽ thông qua trip_seats, vehicle_seats
CREATE TABLE trips (
    trip_id INT PRIMARY KEY AUTO_INCREMENT,
    schedule_id INT NOT NULL,
    driver_id INT NOT NULL,
    assistant_id INT NOT NULL,
    scheduled_departure DATETIME NOT NULL,
    scheduled_arrival DATETIME NOT NULL,
    actual_departure DATETIME,
    actual_arrival DATETIME,
    trip_status ENUM('in_progress', 'completed', 'cancelled') DEFAULT 'in_progress',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    INDEX idx_deleted_at (deleted_at),
    FOREIGN KEY (schedule_id) REFERENCES route_schedules(schedule_id),
    FOREIGN KEY (driver_id) REFERENCES drivers(driver_id),
    FOREIGN KEY (assistant_id) REFERENCES assistants(assistant_id),
    INDEX idx_schedule_id (schedule_id),
    INDEX idx_driver_id (driver_id),
    INDEX idx_assistant_id (assistant_id)
);

-- Create trip_seats table
CREATE TABLE trip_seats (
    trip_seat_id INT PRIMARY KEY AUTO_INCREMENT,
    trip_id INT NOT NULL,
    vehicle_seat_id INT NOT NULL,
    trip_seat_status ENUM('available', 'booked') DEFAULT 'available',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    INDEX idx_deleted_at (deleted_at),
    FOREIGN KEY (trip_id) REFERENCES trips(trip_id),
    FOREIGN KEY (vehicle_seat_id) REFERENCES vehicle_seats(vehicle_seat_id),
    INDEX idx_trip_id (trip_id),
    INDEX idx_vehicle_seat_id (vehicle_seat_id),
    UNIQUE KEY unique_trip_seat (trip_id, vehicle_seat_id)
);

-- Create Invoices table
-- Hóa đơn muốn biết tương ứng với chuyến xe nào thì thông qua invoice_details, trip_seats
CREATE TABLE invoices (
    invoice_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    total_price DECIMAL(10,0) NOT NULL DEFAULT 0.00,
    payment_status ENUM('pending', 'paid') NOT NULL DEFAULT 'pending',
    payment_method ENUM('cash', 'card') NOT NULL DEFAULT 'card',
    invoice_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    INDEX idx_deleted_at (deleted_at),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    INDEX idx_customer_id (customer_id)
);

-- Create InvoiceDetails table
-- Mỗi chi tiết hóa đơn sẽ tương ứng với 1 ghế của chuyến xe đó
CREATE TABLE invoice_details (
    detail_id INT PRIMARY KEY AUTO_INCREMENT,
    invoice_id INT NOT NULL,
    trip_seat_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    INDEX idx_deleted_at (deleted_at),
    FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id),
    FOREIGN KEY (trip_seat_id) REFERENCES trip_seats(trip_seat_id),
    INDEX idx_invoice_id (invoice_id),
    INDEX idx_trip_seat_id (trip_seat_id)
);