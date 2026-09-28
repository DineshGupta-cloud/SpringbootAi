package com.example.banking.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AccountDetailsDto {
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private String status;
    private String currency;
    private LocalDateTime createdAt;
    private String customerName;
}
