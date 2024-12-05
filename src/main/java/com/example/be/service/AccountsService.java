package com.example.be.service;

import com.example.be.model.Accounts;
import com.example.be.repository.AccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountsService {

    @Autowired
    private AccountsRepository accountsRepository;

    public Accounts createAccount(Accounts account) {
        return accountsRepository.save(account);
    }

    public List<Accounts> getAllAccounts() {
        return accountsRepository.findAll();
    }

    public Accounts getAccountById(Integer accountId) {
        return accountsRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public Accounts updateAccount(Integer accountId, Accounts accountDetails) {
        Accounts account = getAccountById(accountId);
        
        account.setUser(accountDetails.getUser());

        return accountsRepository.save(account);
    }

    public void deleteAccount(Integer accountId) {
        Accounts account = getAccountById(accountId);
        accountsRepository.delete(account);
    }
}