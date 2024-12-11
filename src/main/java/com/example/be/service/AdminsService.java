package com.example.be.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.be.model.Admins;
import com.example.be.model.Users;
import com.example.be.repository.AdminsRepository;
import com.example.be.repository.UsersRepository;

@Service
public class AdminsService {

    private final AdminsRepository adminsRepository;
    private final UsersRepository usersRepository;
    private final UsersService usersService;

    // Constructor injection
    public AdminsService(
            AdminsRepository adminsRepository,
            UsersRepository usersRepository,
            UsersService usersService
    ) {
        this.adminsRepository = adminsRepository;
        this.usersRepository = usersRepository;
        this.usersService = usersService;
    }

    @Transactional
    public Admins createAdmin(Admins admin) {
        // Validate required fields
        validateAdminFields(admin);

        // First, create the user associated with the admin
        Users user = usersService.createUserForAdmin(admin.getUser());

        // Set the created user to the admin
        admin.setUser(user);
        admin.setCreatedAt(LocalDateTime.now());

        return adminsRepository.save(admin);
    }

    private void validateAdminFields(Admins admin) {
        if (admin == null) {
            throw new IllegalArgumentException("Admin information cannot be null");
        }

        // Validate user fields
        Users user = admin.getUser();
        if (user == null) {
            throw new IllegalArgumentException("User information is required for admin creation");
        }
    }

    @Transactional
    public Admins updateAdmin(Integer adminId, Admins adminDetails) {
        // Retrieve existing admin
        Admins existingAdmin = getAdminById(adminId);

        // Update user details first
        Users updatedUser = usersService.updateUserForAdmin(
                existingAdmin.getUser().getUserId(),
                adminDetails.getUser()
        );

        // Update admin-specific details if needed
        existingAdmin.setUpdatedAt(LocalDateTime.now());

        // Save updated admin
        return adminsRepository.save(existingAdmin);
    }

    @Transactional
    public void deleteAdmin(Integer adminId) {
        Admins admin = getAdminById(adminId);

        // Soft delete admin
        admin.markAsDeleted();
        adminsRepository.save(admin);

        // Soft delete associated user
        usersService.softDeleteUserForAdmin(admin.getUser().getUserId());
    }

    public Admins getAdminById(Integer adminId) {
        Admins admin = adminsRepository.findByIdNotDeleted(adminId);
        if (admin == null) {
            throw new RuntimeException("Admin not found or has been deleted");
        }
        return admin;
    }

    public List<Admins> getAllAdmins() {
        return adminsRepository.findAllNotDeleted();
    }
}