package com.example.be.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.be.dto.AdminDTO;
import com.example.be.model.Admins;
import com.example.be.service.AdminsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admins")
public class AdminsController {
    private final AdminsService adminsService;

    public AdminsController(AdminsService adminsService) {
        this.adminsService = adminsService;
    }

    @GetMapping
    public ResponseEntity<Page<AdminDTO>> getAllAdmins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String dateOfBirth
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(adminsService.getAllAdminsDTO(
                pageable,
                fullName,
                phoneNumber,
                email,
                gender,
                address,
                dateOfBirth
        ));
    }

    @GetMapping("/{adminId}")
    public ResponseEntity<AdminDTO> getAdminById(@PathVariable Integer adminId) {
        return ResponseEntity.ok(adminsService.getAdminDTOById(adminId));
    }

    @PostMapping
    public ResponseEntity<AdminDTO> createAdmin(@Valid @RequestBody Admins admin) {
        return ResponseEntity.ok(adminsService.createAdmin(admin));
    }

    @PutMapping("/{adminId}")
    public ResponseEntity<AdminDTO> updateAdmin(@PathVariable Integer adminId, @Valid @RequestBody Admins admin) {
        return ResponseEntity.ok(adminsService.updateAdmin(adminId, admin));
    }

    @DeleteMapping("/{adminId}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Integer adminId) {
        adminsService.deleteAdmin(adminId);
        return ResponseEntity.noContent().build();
    }
}