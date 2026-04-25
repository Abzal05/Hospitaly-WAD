package com.example.demo.service.impl;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.factory.AccountFactory;
import com.example.demo.model.Account;
import com.example.demo.model.AccountType;
import com.example.demo.model.User;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountFactory accountFactory;

    public AccountServiceImpl(AccountRepository accountRepository,
                               UserRepository userRepository,
                               AccountFactory accountFactory) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.accountFactory = accountFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> findByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Счёт не найден: id=" + id));
    }

    @Override
    public Account createForUser(Long userId, AccountType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: id=" + userId));
        Account account = accountFactory.create(type, user);
        return accountRepository.save(account);
    }

    @Override
    public void delete(Long id) {
        Account account = findById(id);
        accountRepository.delete(account);
    }
}
