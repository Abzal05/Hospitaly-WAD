package com.example.demo.model;

public enum AccountType {
    SAVINGS("Сберегательный"),
    CREDIT("Кредитный"),
    DEBIT("Дебетовый");

    private final String displayName;

    AccountType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
