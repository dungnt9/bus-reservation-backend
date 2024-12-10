package com.example.be.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.be.model.Accounts;
import com.example.be.service.AccountsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/accounts")
public class AccountsController {

    private final AccountsService accountsService;

    public AccountsController(AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    @GetMapping
    public ResponseEntity<List<Accounts>> getAllAccounts() {
        return ResponseEntity.ok(accountsService.getAllAccounts());
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Accounts> getAccountById(@PathVariable Integer accountId) {
        return ResponseEntity.ok(accountsService.getAccountById(accountId));
    }

    @PostMapping
    public ResponseEntity<Accounts> createAccount(@Valid @RequestBody Accounts account) {
        return ResponseEntity.ok(accountsService.createAccount(account));
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<Accounts> updateAccount(@PathVariable Integer accountId, @Valid @RequestBody Accounts account) {
        return ResponseEntity.ok(accountsService.updateAccount(accountId, account));
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Integer accountId) {
        accountsService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }
}
