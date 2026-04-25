package com.example.demo.repository;

import com.example.demo.model.Transaction;
import com.example.demo.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountId(Long accountId);
    List<Transaction> findByStatus(TransactionStatus status);
}
