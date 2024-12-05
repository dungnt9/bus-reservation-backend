package com.example.be.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.be.model.Admins;
import com.example.be.service.AdminsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admins")
public class AdminsController {

    @Autowired
    private AdminsService adminsService;

    @PostMapping
    public ResponseEntity<Admins> createAdmin(@Valid @RequestBody Admins admin) {
        return ResponseEntity.ok(adminsService.createAdmin(admin));
    }

    @GetMapping
    public ResponseEntity<List<Admins>> getAllAdmins() {
        return ResponseEntity.ok(adminsService.getAllAdmins());
    }

    @GetMapping("/{adminId}")
    public ResponseEntity<Admins> getAdminById(@PathVariable Integer adminId) {
        return ResponseEntity.ok(adminsService.getAdminById(adminId));
    }

    @PutMapping("/{adminId}")
    public ResponseEntity<Admins> updateAdmin(@PathVariable Integer adminId, @Valid @RequestBody Admins admin) {
        return ResponseEntity.ok(adminsService.updateAdmin(adminId, admin));
    }

    @DeleteMapping("/{adminId}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Integer adminId) {
        adminsService.deleteAdmin(adminId);
        return ResponseEntity.noContent().build();
    }
}