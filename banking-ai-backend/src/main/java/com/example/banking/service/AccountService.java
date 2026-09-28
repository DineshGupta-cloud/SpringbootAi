package com.example.banking.service;

import com.example.banking.dto.*;
import com.example.banking.entity.Account;
import com.example.banking.entity.Customer;
import com.example.banking.entity.Transaction;
import com.example.banking.exception.ApiException;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.CustomerRepository;
import com.example.banking.repository.TransactionRepository;
import com.example.banking.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final SecurityService securityService;

    public List<AccountDetailsDto> getAccounts() {
        Long customerId = securityService.getCurrentCustomerId();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ApiException("Customer not found", HttpStatus.NOT_FOUND));
        return accountRepository.findByCustomerId(customerId).stream()
                .map(a -> toDetailsDto(a, customer.getName())).collect(Collectors.toList());
    }

    public AccountBalanceDto getBalance() {
        Account account = getPrimaryAccount();
        return AccountBalanceDto.builder()
                .accountNumber(mask(account.getAccountNumber()))
                .balance(account.getBalance()).currency("INR").build();
    }

    public AccountDetailsDto getAccountDetails() {
        Account account = getPrimaryAccount();
        Customer customer = customerRepository.findById(account.getCustomerId())
                .orElseThrow(() -> new ApiException("Customer not found", HttpStatus.NOT_FOUND));
        return toDetailsDto(account, customer.getName());
    }

    public List<TransactionDto> getRecentTransactions(int limit) {
        if (limit < 1 || limit > 50) limit = 5;
        Account account = getPrimaryAccount();
        return transactionRepository.findByAccountIdOrderByTransactionDateDesc(account.getId(), PageRequest.of(0, limit))
                .stream().map(this::toTxDto).collect(Collectors.toList());
    }

    public List<TransactionDto> getStatement(LocalDate from, LocalDate to) {
        if (from == null || to == null)
            throw new ApiException("Both from and to dates are required", HttpStatus.BAD_REQUEST);
        if (from.isAfter(to))
            throw new ApiException("Invalid date range: 'from' must be before or equal to 'to'", HttpStatus.BAD_REQUEST);
        Account account = getPrimaryAccount();
        return transactionRepository.findByAccountIdAndDateRange(
                account.getId(), from.atStartOfDay(), to.atTime(LocalTime.MAX))
                .stream().map(this::toTxDto).collect(Collectors.toList());
    }

    private Account getPrimaryAccount() {
        Long customerId = securityService.getCurrentCustomerId();
        return accountRepository.findByCustomerIdAndStatus(customerId, "ACTIVE")
                .orElseThrow(() -> new ApiException("No active account found", HttpStatus.NOT_FOUND));
    }

    private AccountDetailsDto toDetailsDto(Account a, String name) {
        return AccountDetailsDto.builder()
                .accountNumber(mask(a.getAccountNumber())).accountType(a.getAccountType())
                .balance(a.getBalance()).status(a.getStatus()).currency("INR")
                .createdAt(a.getCreatedAt()).customerName(name).build();
    }

    private TransactionDto toTxDto(Transaction t) {
        return TransactionDto.builder().id(t.getId()).transactionDate(t.getTransactionDate())
                .transactionType(t.getTransactionType()).amount(t.getAmount())
                .description(t.getDescription()).referenceNumber(t.getReferenceNumber())
                .balanceAfterTransaction(t.getBalanceAfterTransaction()).build();
    }

    private String mask(String n) {
        if (n == null || n.length() < 4) return "****";
        return "****" + n.substring(n.length() - 4);
    }
}
