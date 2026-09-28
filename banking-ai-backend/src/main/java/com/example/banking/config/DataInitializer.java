package com.example.banking.config;

import com.example.banking.entity.Account;
import com.example.banking.entity.Customer;
import com.example.banking.entity.Transaction;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.CustomerRepository;
import com.example.banking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (customerRepository.count() > 0) {
            log.info("Sample data already present, skipping initialization");
            return;
        }
        log.info("Initializing sample banking data...");

        Customer dinesh = customerRepository.save(Customer.builder()
                .name("Dinesh").email("customer@example.com")
                .password(passwordEncoder.encode("password")).phone("9876543210").build());

        Customer priya = customerRepository.save(Customer.builder()
                .name("Priya").email("priya@example.com")
                .password(passwordEncoder.encode("password")).phone("9876543211").build());

        Account account1 = accountRepository.save(Account.builder()
                .customerId(dinesh.getId()).accountNumber("100012345678")
                .accountType("SAVINGS").balance(new BigDecimal("45250.00")).status("ACTIVE").build());

        accountRepository.save(Account.builder()
                .customerId(priya.getId()).accountNumber("100012345679")
                .accountType("SAVINGS").balance(new BigDecimal("78500.00")).status("ACTIVE").build());

        List<Transaction> txs = List.of(
                tx(account1.getId(), LocalDateTime.of(2026, 9, 25, 14, 30), "DEBIT", "1500.00", "UPI Payment", "TXN001", "43750.00"),
                tx(account1.getId(), LocalDateTime.of(2026, 9, 24, 9, 0), "CREDIT", "5000.00", "Salary", "TXN002", "45250.00"),
                tx(account1.getId(), LocalDateTime.of(2026, 9, 22, 18, 15), "DEBIT", "750.00", "ATM Withdrawal", "TXN003", "40250.00"),
                tx(account1.getId(), LocalDateTime.of(2026, 9, 20, 11, 0), "DEBIT", "1200.00", "Online Shopping", "TXN004", "41000.00"),
                tx(account1.getId(), LocalDateTime.of(2026, 9, 18, 16, 45), "CREDIT", "2000.00", "Refund", "TXN005", "42200.00"),
                tx(account1.getId(), LocalDateTime.of(2026, 9, 15, 10, 30), "DEBIT", "500.00", "Mobile Recharge", "TXN006", "40200.00"),
                tx(account1.getId(), LocalDateTime.of(2026, 9, 12, 13, 0), "DEBIT", "2500.00", "Electricity Bill", "TXN007", "40700.00"),
                tx(account1.getId(), LocalDateTime.of(2026, 9, 10, 8, 0), "CREDIT", "10000.00", "Freelance Payment", "TXN008", "43200.00"),
                tx(account1.getId(), LocalDateTime.of(2026, 9, 5, 19, 20), "DEBIT", "800.00", "Restaurant", "TXN009", "33200.00"),
                tx(account1.getId(), LocalDateTime.of(2026, 9, 1, 12, 0), "CREDIT", "3000.00", "Interest Credit", "TXN010", "34000.00"),
                tx(account1.getId(), LocalDateTime.of(2026, 8, 28, 15, 0), "DEBIT", "2000.00", "Fuel", "TXN011", "31000.00")
        );
        transactionRepository.saveAll(txs);
        log.info("Sample data initialized: 2 customers, 2 accounts, {} transactions", txs.size());
    }

    private Transaction tx(Long accountId, LocalDateTime date, String type, String amount,
                           String desc, String ref, String balanceAfter) {
        return Transaction.builder().accountId(accountId).transactionDate(date)
                .transactionType(type).amount(new BigDecimal(amount)).description(desc)
                .referenceNumber(ref).balanceAfterTransaction(new BigDecimal(balanceAfter)).build();
    }
}
