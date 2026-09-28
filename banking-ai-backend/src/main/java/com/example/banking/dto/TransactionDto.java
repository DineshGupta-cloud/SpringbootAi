package com.example.banking.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TransactionDto {
    private Long id;
    private LocalDateTime transactionDate;
    private String transactionType;
    private BigDecimal amount;
    private String description;
    private String referenceNumber;
    private BigDecimal balanceAfterTransaction;
}
