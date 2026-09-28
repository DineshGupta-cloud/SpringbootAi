package com.example.banking.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AccountBalanceDto {
    private String accountNumber;
    private BigDecimal balance;
    private String currency;
}
