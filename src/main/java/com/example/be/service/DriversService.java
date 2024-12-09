package com.example.be.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.be.model.Drivers;
import com.example.be.model.Users;
import com.example.be.repository.DriversRepository;
import com.example.be.repository.UserRepository;
import org.springframework.util.StringUtils;

@Service
public class DriversService {

    @Autowired
    private DriversRepository driversRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public Drivers createDriver(Drivers driver) {
        // Validate required fields
        validateDriverFields(driver);

        // First, create the user associated with the driver
        Users user = userService.createUserForDriver(driver.getUser());

        // Set the created user to the driver
        driver.setUser(user);
        driver.setCreatedAt(LocalDateTime.now());
        driver.setDriverStatus(Drivers.DriverStatus.available); // Default status

        return driversRepository.save(driver);
    }

    private void validateDriverFields(Drivers driver) {
        if (driver == null) {
            throw new IllegalArgumentException("Driver information cannot be null");
        }

        // Validate user fields
        Users user = driver.getUser();
        if (user == null ||
                !StringUtils.hasText(user.getFullName()) ||
                !StringUtils.hasText(user.getPhoneNumber())) {
            throw new IllegalArgumentException("User full name and phone number are required");
        }

        // Validate driver-specific fields
        if (!StringUtils.hasText(driver.getLicenseNumber()) ||
                !StringUtils.hasText(driver.getLicenseClass()) ||
                driver.getLicenseExpiry() == null) {
            throw new IllegalArgumentException("License number, class, and expiry are required");
        }
    }

    @Transactional
    public Drivers updateDriver(Integer driverId, Drivers driverDetails) {
        // Retrieve existing driver
        Drivers existingDriver = getDriverById(driverId);

        // Update user details first
        Users updatedUser = userService.updateUserForDriver(existingDriver.getUser().getUserId(), driverDetails.getUser());

        // Update driver-specific details
        existingDriver.setLicenseNumber(driverDetails.getLicenseNumber());
        existingDriver.setLicenseClass(driverDetails.getLicenseClass());
        existingDriver.setLicenseExpiry(driverDetails.getLicenseExpiry());
        existingDriver.setDriverStatus(driverDetails.getDriverStatus());
        existingDriver.setUpdatedAt(LocalDateTime.now());

        // Save updated driver
        return driversRepository.save(existingDriver);
    }

    @Transactional
    public void deleteDriver(Integer driverId) {
        Drivers driver = getDriverById(driverId);

        // Soft delete driver
        driver.markAsDeleted();
        driversRepository.save(driver);

        // Soft delete associated user
        userService.softDeleteUserForDriver(driver.getUser().getUserId());
    }

    public Drivers getDriverById(Integer driverId) {
        Drivers driver = driversRepository.findByIdNotDeleted(driverId);
        if (driver == null) {
            throw new RuntimeException("Driver not found or has been deleted");
        }
        return driver;
    }

    public List<Drivers> getAllDrivers() {
        return driversRepository.findAllNotDeleted();
    }
}