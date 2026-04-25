package com.example.demo.service;

import com.example.demo.model.Account;
import com.example.demo.model.AccountType;

import java.util.List;

public interface AccountService {
    List<Account> findAll();
    List<Account> findByUserId(Long userId);
    Account findById(Long id);
    Account createForUser(Long userId, AccountType type);
    void delete(Long id);
}
