package com.example.be.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.be.dto.AdminDTO;
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

    protected AdminDTO convertToDTO(Admins admin) {
        AdminDTO dto = new AdminDTO();
        dto.setAdminId(admin.getAdminId());
        dto.setUserId(admin.getUser().getUserId());
        dto.setFullName(admin.getUser().getFullName());
        dto.setPhoneNumber(admin.getUser().getPhoneNumber());
        dto.setEmail(admin.getUser().getEmail());
        dto.setPassword_hash(admin.getUser().getPassword_hash());
        dto.setGender(admin.getUser().getGender() != null ? admin.getUser().getGender().toString() : null);
        dto.setAddress(admin.getUser().getAddress());
        dto.setDateOfBirth(admin.getUser().getDateOfBirth());
        return dto;
    }

    public Page<AdminDTO> getAllAdminsDTO(Pageable pageable) {
        Page<Admins> adminPage = adminsRepository.findAllNotDeleted(pageable);
        List<AdminDTO> adminDTOs = adminPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                adminDTOs,
                pageable,
                adminPage.getTotalElements()
        );
    }

    public AdminDTO getAdminDTOById(Integer adminId) {
        return convertToDTO(getAdminById(adminId));
    }

    public AdminDTO createAdmin(Admins admin) {
        return convertToDTO(createAdminEntity(admin));
    }

    @Transactional
    protected Admins createAdminEntity(Admins admin) {
        validateAdminFields(admin);
        Users user = usersService.createUserForAdmin(admin.getUser());
        admin.setUser(user);
        admin.setCreatedAt(LocalDateTime.now());
        return adminsRepository.save(admin);
    }

    protected void validateAdminFields(Admins admin) {
        if (admin == null) {
            throw new IllegalArgumentException("Admin information cannot be null");
        }

        // Validate user fields
        Users user = admin.getUser();
        if (user == null) {
            throw new IllegalArgumentException("User information is required for admin creation");
        }
    }

    public AdminDTO updateAdmin(Integer adminId, Admins adminDetails) {
        return convertToDTO(updateAdminEntity(adminId, adminDetails));
    }

    @Transactional
    protected Admins updateAdminEntity(Integer adminId, Admins adminDetails) {
        Admins existingAdmin = getAdminById(adminId);
        usersService.updateUserForAdmin(
                existingAdmin.getUser().getUserId(),
                adminDetails.getUser()
        );
        existingAdmin.setUpdatedAt(LocalDateTime.now());
        return adminsRepository.save(existingAdmin);
    }

    @Transactional
    public void deleteAdmin(Integer adminId) {
        Admins admin = getAdminById(adminId);
        admin.markAsDeleted();
        adminsRepository.save(admin);
        usersService.softDeleteUserForAdmin(admin.getUser().getUserId());
    }

    public Admins getAdminById(Integer adminId) {
        Admins admin = adminsRepository.findByIdNotDeleted(adminId);
        if (admin == null) {
            throw new RuntimeException("Admin not found or has been deleted");
        }
        return admin;
    }
}