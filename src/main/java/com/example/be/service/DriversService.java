package com.example.be.service;

import com.example.be.model.Drivers;
import com.example.be.repository.DriversRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriversService {

    @Autowired
    private DriversRepository driversRepository;

    public Drivers createDriver(Drivers driver) {
        return driversRepository.save(driver);
    }

    public List<Drivers> getAllDrivers() {
        return driversRepository.findAll();
    }

    public Drivers getDriverById(Integer driverId) {
        return driversRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
    }

    public Drivers updateDriver(Integer driverId, Drivers driverDetails) {
        Drivers driver = getDriverById(driverId);
        
        driver.setUser(driverDetails.getUser());
        driver.setLicenseNumber(driverDetails.getLicenseNumber());
        driver.setLicenseClass(driverDetails.getLicenseClass());
        driver.setLicenseExpiry(driverDetails.getLicenseExpiry());
        driver.setDriverStatus(driverDetails.getDriverStatus());

        return driversRepository.save(driver);
    }

    public void deleteDriver(Integer driverId) {
        Drivers driver = getDriverById(driverId);
        driversRepository.delete(driver);
    }
}