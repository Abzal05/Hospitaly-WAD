package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    private String description;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;

    @Builder.Default
    private Instant createdAt = Instant.now();

    public void complete() {
        if (!this.status.canTransitionTo(TransactionStatus.COMPLETED)) {
            throw new IllegalStateException("Transaction cannot be completed from state: " + this.status);
        }
        this.status = TransactionStatus.COMPLETED;
    }

    public void fail() {
        if (!this.status.canTransitionTo(TransactionStatus.FAILED)) {
            throw new IllegalStateException("Transaction cannot be failed from state: " + this.status);
        }
        this.status = TransactionStatus.FAILED;
    }
}
