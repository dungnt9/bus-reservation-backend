package com.example.be.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.be.dto.DriverDTO;
import com.example.be.model.Drivers;
import com.example.be.model.Users;
import com.example.be.repository.DriversRepository;
import com.example.be.repository.UsersRepository;

@Service
public class DriversService {

    private final DriversRepository driversRepository;
    private final UsersRepository usersRepository;
    private final UsersService usersService;

    public DriversService(DriversRepository driversRepository,
                          UsersRepository usersRepository,
                          UsersService usersService) {
        this.driversRepository = driversRepository;
        this.usersRepository = usersRepository;
        this.usersService = usersService;
    }

    protected DriverDTO convertToDTO(Drivers driver) {
        DriverDTO dto = new DriverDTO();
        dto.setDriverId(driver.getDriverId());
        dto.setUserId(driver.getUser().getUserId());
        dto.setFullName(driver.getUser().getFullName());
        dto.setPhoneNumber(driver.getUser().getPhoneNumber());
        dto.setEmail(driver.getUser().getEmail());
        dto.setPassword_hash(driver.getUser().getPassword_hash());
        dto.setGender(driver.getUser().getGender() != null ? driver.getUser().getGender().toString() : null);
        dto.setAddress(driver.getUser().getAddress());
        dto.setDateOfBirth(driver.getUser().getDateOfBirth());
        dto.setLicenseNumber(driver.getLicenseNumber());
        dto.setLicenseClass(driver.getLicenseClass());
        dto.setLicenseExpiry(driver.getLicenseExpiry());
        dto.setDriverStatus(driver.getDriverStatus().toString());
        return dto;
    }

    public Page<DriverDTO> getAllDriversDTO(
            Pageable pageable,
            String fullName,
            String phoneNumber,
            String email,
            String gender,
            String address,
            String dateOfBirth,
            String licenseNumber,
            String licenseClass,
            String licenseExpiry,
            String driverStatus
    ) {
        Specification<Drivers> spec = (root, query, criteriaBuilder) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (fullName != null && !fullName.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("user").get("fullName")),
                        "%" + fullName.toLowerCase() + "%"
                ));
            }

            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        root.get("user").get("phoneNumber"),
                        "%" + phoneNumber + "%"
                ));
            }

            if (email != null && !email.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("user").get("email")),
                        "%" + email.toLowerCase() + "%"
                ));
            }

            if (gender != null && !gender.isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("user").get("gender"),
                        Users.Gender.valueOf(gender.toLowerCase())
                ));
            }

            if (address != null && !address.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("user").get("address")),
                        "%" + address.toLowerCase() + "%"
                ));
            }

            if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
                try {
                    LocalDate date = LocalDate.parse(dateOfBirth);
                    predicates.add(criteriaBuilder.equal(root.get("user").get("dateOfBirth"), date));
                } catch (Exception e) {
                    System.err.println("Invalid date format: " + dateOfBirth);
                }
            }

            if (licenseNumber != null && !licenseNumber.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        root.get("licenseNumber"),
                        "%" + licenseNumber + "%"
                ));
            }

            if (licenseClass != null && !licenseClass.isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("licenseClass"),
                        licenseClass.toUpperCase()
                ));
            }

            if (licenseExpiry != null && !licenseExpiry.isEmpty()) {
                try {
                    LocalDate date = LocalDate.parse(licenseExpiry);
                    predicates.add(criteriaBuilder.equal(root.get("licenseExpiry"), date));
                } catch (Exception e) {
                    System.err.println("Invalid date format: " + licenseExpiry);
                }
            }

            if (driverStatus != null && !driverStatus.isEmpty()) {
                try {
                    Drivers.DriverStatus status = Drivers.DriverStatus.valueOf(driverStatus.toLowerCase());
                    predicates.add(criteriaBuilder.equal(root.get("driverStatus"), status));
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid driver status: " + driverStatus);
                }
            }

            predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Page<Drivers> driverPage = driversRepository.findAll(spec, pageable);
        return driverPage.map(this::convertToDTO);
    }

    public DriverDTO getDriverDTOById(Integer driverId) {
        return convertToDTO(getDriverById(driverId));
    }

    public DriverDTO createDriver(Drivers driver) {
        return convertToDTO(createDriverEntity(driver));
    }

    @Transactional
    protected Drivers createDriverEntity(Drivers driver) {
        validateDriverFields(driver);
        Users user = usersService.createUserForDriver(driver.getUser());
        driver.setUser(user);
        driver.setCreatedAt(LocalDateTime.now());
        driver.setDriverStatus(Drivers.DriverStatus.available);
        return driversRepository.save(driver);
    }

    protected void validateDriverFields(Drivers driver) {
        if (driver == null) {
            throw new IllegalArgumentException("Driver information cannot be null");
        }
        Users user = driver.getUser();
        if (user == null ||
                !StringUtils.hasText(user.getFullName()) ||
                !StringUtils.hasText(user.getPhoneNumber())) {
            throw new IllegalArgumentException("User full name and phone number are required");
        }
        if (!StringUtils.hasText(driver.getLicenseNumber()) ||
                !StringUtils.hasText(driver.getLicenseClass()) ||
                driver.getLicenseExpiry() == null) {
            throw new IllegalArgumentException("License number, class, and expiry are required");
        }
    }

    public DriverDTO updateDriver(Integer driverId, Drivers driverDetails) {
        return convertToDTO(updateDriverEntity(driverId, driverDetails));
    }

    @Transactional
    protected Drivers updateDriverEntity(Integer driverId, Drivers driverDetails) {
        Drivers existingDriver = getDriverById(driverId);
        Users updatedUser = usersService.updateUserForDriver(
                existingDriver.getUser().getUserId(),
                driverDetails.getUser()
        );
        existingDriver.setLicenseNumber(driverDetails.getLicenseNumber());
        existingDriver.setLicenseClass(driverDetails.getLicenseClass());
        existingDriver.setLicenseExpiry(driverDetails.getLicenseExpiry());
        existingDriver.setDriverStatus(driverDetails.getDriverStatus());
        existingDriver.setUpdatedAt(LocalDateTime.now());
        return driversRepository.save(existingDriver);
    }

    public Drivers getDriverById(Integer driverId) {
        Drivers driver = driversRepository.findByIdNotDeleted(driverId);
        if (driver == null) {
            throw new RuntimeException("Driver not found or has been deleted");
        }
        return driver;
    }
}