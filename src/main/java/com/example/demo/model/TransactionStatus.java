package com.example.demo.model;

public enum TransactionStatus {
    PENDING,
    COMPLETED,
    FAILED;

    public boolean canTransitionTo(TransactionStatus next) {
        return this == PENDING && (next == COMPLETED || next == FAILED);
    }
}
