package com.example.banking.controller;

import com.example.banking.dto.*;
import com.example.banking.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountDetailsDto>> getAccounts() {
        return ResponseEntity.ok(accountService.getAccounts());
    }

    @GetMapping("/balance")
    public ResponseEntity<AccountBalanceDto> getBalance() {
        return ResponseEntity.ok(accountService.getBalance());
    }

    @GetMapping("/details")
    public ResponseEntity<AccountDetailsDto> getAccountDetails() {
        return ResponseEntity.ok(accountService.getAccountDetails());
    }

    @GetMapping("/transactions/recent")
    public ResponseEntity<List<TransactionDto>> getRecentTransactions(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(accountService.getRecentTransactions(limit));
    }

    @GetMapping("/statement")
    public ResponseEntity<List<TransactionDto>> getStatement(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(accountService.getStatement(from, to));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDto>> getTransactions(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(accountService.getRecentTransactions(limit));
    }
}
