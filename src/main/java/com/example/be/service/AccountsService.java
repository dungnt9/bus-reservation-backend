package com.example.be.service;

import com.example.be.model.Accounts;
import com.example.be.repository.AccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountsService {

    @Autowired
    private AccountsRepository accountsRepository;

    public Accounts createAccount(Accounts account) {
        account.setCreatedAt(LocalDateTime.now());
        return accountsRepository.save(account);
    }

    public List<Accounts> getAllAccounts() {
        return accountsRepository.findAllNotDeleted(); // Assuming similar custom method
    }

    public Accounts getAccountById(Integer accountId) {
        Accounts account = accountsRepository.findByIdNotDeleted(accountId);
        if (account == null) {
            throw new RuntimeException("Account not found or has been deleted");
        }
        return account;
    }

    public Accounts updateAccount(Integer accountId, Accounts accountDetails) {
        Accounts account = getAccountById(accountId);

        account.setUser(accountDetails.getUser());
        account.setUpdatedAt(LocalDateTime.now());

        return accountsRepository.save(account);
    }

    public void deleteAccount(Integer accountId) {
        Accounts account = getAccountById(accountId);
        account.markAsDeleted(); // Assuming soft delete method similar to DriversService
        accountsRepository.save(account);
    }
}