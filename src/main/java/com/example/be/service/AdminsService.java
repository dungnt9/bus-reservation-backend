package com.example.be.service;

import com.example.be.model.Admins;
import com.example.be.repository.AdminsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminsService {

    @Autowired
    private AdminsRepository adminsRepository;

    public Admins createAdmin(Admins admin) {
        admin.setCreatedAt(LocalDateTime.now());
        return adminsRepository.save(admin);
    }

    public List<Admins> getAllAdmins() {
        return adminsRepository.findAllNotDeleted(); // Assuming similar custom method
    }

    public Admins getAdminById(Integer adminId) {
        Admins admin = adminsRepository.findByIdNotDeleted(adminId);
        if (admin == null) {
            throw new RuntimeException("Admin not found or has been deleted");
        }
        return admin;
    }

    public Admins updateAdmin(Integer adminId, Admins adminDetails) {
        Admins admin = getAdminById(adminId);

        admin.setUser(adminDetails.getUser());
        admin.setUpdatedAt(LocalDateTime.now());

        return adminsRepository.save(admin);
    }

    public void deleteAdmin(Integer adminId) {
        Admins admin = getAdminById(adminId);
        admin.markAsDeleted(); // Assuming soft delete method similar to DriversService
        adminsRepository.save(admin);
    }
}