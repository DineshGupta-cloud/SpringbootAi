package com.example.banking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Transaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "account_id", nullable = false) private Long accountId;
    @Column(name = "transaction_date", nullable = false) private LocalDateTime transactionDate;
    @Column(name = "transaction_type", nullable = false) private String transactionType;
    @Column(nullable = false, precision = 15, scale = 2) private BigDecimal amount;
    private String description;
    @Column(name = "reference_number") private String referenceNumber;
    @Column(name = "balance_after_transaction", precision = 15, scale = 2) private BigDecimal balanceAfterTransaction;
}
