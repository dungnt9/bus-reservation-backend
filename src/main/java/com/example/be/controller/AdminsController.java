package com.example.be.controller;

import java.util.List; //thao tác với danh sách các phần tử

import org.springframework.http.ResponseEntity;   //phản hồi HTTP: Body, Headers, HTTP Status

import org.springframework.web.bind.annotation.GetMapping;  //Đánh dấu các phương thức xử lý HTTP request nào
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.PathVariable; //lấy giá trị từ URL để làm tham số method
import org.springframework.web.bind.annotation.RequestBody; //Lấy dữ liệu từ request body vào một object để create/ update
import org.springframework.web.bind.annotation.RequestMapping; //Base URL /api/admins cho tất cả các endpoint
import org.springframework.web.bind.annotation.RestController; //nói class này là 1 RESTful Controller

import com.example.be.model.Admins;
import com.example.be.service.AdminsService;
import com.example.be.dto.AdminDTO;

import jakarta.validation.Valid; //kích hoạt validate annotation validation (@NotNull,...) được định nghĩa trong class

@RestController
@RequestMapping("/api/admins")
public class AdminsController {
    private final AdminsService adminsService;

    public AdminsController(AdminsService adminsService) {
        this.adminsService = adminsService;
    }

    @GetMapping
    public ResponseEntity<List<AdminDTO>> getAllAdmins() {
        return ResponseEntity.ok(adminsService.getAllAdminsDTO());
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
