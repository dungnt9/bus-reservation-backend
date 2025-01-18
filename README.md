# Bus Reservation System Backend

A Spring Boot backend application for managing bus transportation and ticket reservations.

---

## Features
- User authentication and authorization with JWT
- Role-based access control (Admin, Driver, Assistant, Customer)
- Bus route and schedule management
- Trip scheduling and management
- Vehicle and seat management
- Ticket booking and invoicing
- Driver and assistant assignment
- Payment processing (Cash/Card)

---

## Tech Stack
- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Security**
- **Spring Data JPA**
- **MySQL Database**
- **Maven**
- **JWT Authentication**
- **RESTful APIs**

---

## Key Components

### Models
- Users (Admin, Driver, Assistant, Customer)
- Routes & RouteSchedules
- Trips & TripSeats
- Vehicles
- Invoices & InvoiceDetails

---

## Main Features

### Route Management
- CRUD operations for bus routes
- Schedule management with departure times and days
- Route status tracking (active/inactive)

### Trip Management
- Automated trip creation from schedules
- Driver and assistant assignment
- Vehicle allocation
- Seat management
- Trip status tracking

### Booking System
- Seat reservation
- Invoice generation
- Payment processing
- Booking status management

### Security
- JWT-based authentication
- Role-based access control
- Secure password handling
- Phone number verification

---

## Configuration

The application uses standard Spring Boot configuration with properties defined in `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/db
spring.jpa.hibernate.ddl-auto=update
spring.data.web.pageable.default-page-size=10
```

## Build & Run

Built with Maven:

```sh
mvn clean install
mvn spring-boot:run
```
