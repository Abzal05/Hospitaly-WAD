package com.example.demo.factory;

import com.example.demo.model.Account;
import com.example.demo.model.AccountType;
import com.example.demo.model.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AccountFactory {

    public Account create(AccountType type, User user) {
        return Account.builder()
                .accountType(type)
                .user(user)
                .accountNumber(generateNumber(type))
                .balance(defaultBalance(type))
                .build();
    }

    private String generateNumber(AccountType type) {
        String prefix = switch (type) {
            case SAVINGS -> "SAV";
            case CREDIT  -> "CRD";
            case DEBIT   -> "DBT";
        };
        return prefix + "-" + System.currentTimeMillis();
    }

    private BigDecimal defaultBalance(AccountType type) {
        return switch (type) {
            case CREDIT  -> new BigDecimal("5000.00");
            default      -> BigDecimal.ZERO;
        };
    }
}
