package com.example.be.service;

import com.example.be.model.Admins;
import com.example.be.repository.AdminsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminsService {

    @Autowired
    private AdminsRepository adminsRepository;

    public Admins createAdmin(Admins admin) {
        return adminsRepository.save(admin);
    }

    public List<Admins> getAllAdmins() {
        return adminsRepository.findAll();
    }

    public Admins getAdminById(Integer adminId) {
        return adminsRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    public Admins updateAdmin(Integer adminId, Admins adminDetails) {
        Admins admin = getAdminById(adminId);
        
        // Update the user reference instead of user ID
        admin.setUser(adminDetails.getUser());

        return adminsRepository.save(admin);
    }

    public void deleteAdmin(Integer adminId) {
        Admins admin = getAdminById(adminId);
        adminsRepository.delete(admin);
    }
}