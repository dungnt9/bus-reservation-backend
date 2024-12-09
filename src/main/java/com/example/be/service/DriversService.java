package com.example.be.service;

import com.example.be.model.Drivers;
import com.example.be.repository.DriversRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;                  //
import java.util.List;

@Service
public class DriversService {

    @Autowired
    private DriversRepository driversRepository;

    public Drivers createDriver(Drivers driver) {
        driver.setCreatedAt(LocalDateTime.now());                   //
        return driversRepository.save(driver);
    }

    public List<Drivers> getAllDrivers() {
        return driversRepository.findAllNotDeleted();               //
    }

    public Drivers getDriverById(Integer driverId) {
        Drivers driver = driversRepository.findByIdNotDeleted(driverId);          //
        if (driver == null) {                                                    //
            throw new RuntimeException("Driver not found or has been deleted");        //
        }                                                                           //
        return driver;                                                             //
    }

    public Drivers updateDriver(Integer driverId, Drivers driverDetails) {
        Drivers driver = getDriverById(driverId);

        driver.setUser(driverDetails.getUser());
        driver.setLicenseNumber(driverDetails.getLicenseNumber());
        driver.setLicenseClass(driverDetails.getLicenseClass());
        driver.setLicenseExpiry(driverDetails.getLicenseExpiry());
        driver.setDriverStatus(driverDetails.getDriverStatus());
        driver.setUpdatedAt(LocalDateTime.now());

        return driversRepository.save(driver);
    }

    public void deleteDriver(Integer driverId) {
        Drivers driver = getDriverById(driverId);
        driver.markAsDeleted(); // Sử dụng soft delete              //
        driversRepository.save(driver);                             //
    }
}