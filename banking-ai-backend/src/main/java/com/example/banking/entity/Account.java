package com.example.banking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Account {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "customer_id", nullable = false) private Long customerId;
    @Column(name = "account_number", nullable = false, unique = true) private String accountNumber;
    @Column(name = "account_type", nullable = false) private String accountType;
    @Column(nullable = false, precision = 15, scale = 2) private BigDecimal balance;
    @Column(nullable = false) private String status;
    @Column(name = "created_at") private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
}
