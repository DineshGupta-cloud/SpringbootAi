package com.example.banking.ai;

import com.example.banking.dto.*;
import com.example.banking.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BankingTools {
    private final AccountService accountService;

    @Tool(description = "Get the authenticated customer's current account balance. Returns masked account number, balance and currency INR.")
    public AccountBalanceDto getBalance() {
        return accountService.getBalance();
    }

    @Tool(description = "Get the authenticated customer's account details including account type, status, balance and customer name.")
    public AccountDetailsDto getAccountDetails() {
        return accountService.getAccountDetails();
    }

    @Tool(description = "Get the authenticated customer's most recent transactions. Use limit to control how many (default 5, max 20).")
    public String getRecentTransactions(
            @ToolParam(description = "Number of recent transactions to return, between 1 and 20") Integer limit) {
        int lim = (limit == null || limit < 1) ? 5 : Math.min(limit, 20);
        return formatTransactions(accountService.getRecentTransactions(lim));
    }

    @Tool(description = "Get account statement (transactions) for a date range. Dates must be in YYYY-MM-DD format.")
    public String getStatement(
            @ToolParam(description = "Start date in YYYY-MM-DD format") String fromDate,
            @ToolParam(description = "End date in YYYY-MM-DD format") String toDate) {
        LocalDate from = LocalDate.parse(fromDate);
        LocalDate to = LocalDate.parse(toDate);
        List<TransactionDto> txs = accountService.getStatement(from, to);
        if (txs.isEmpty()) return "No transactions found between " + fromDate + " and " + toDate + ".";
        return formatTransactions(txs);
    }

    private String formatTransactions(List<TransactionDto> txs) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");
        return txs.stream().map(t -> String.format("%s - %s ₹%,.2f - %s",
                t.getTransactionDate().format(fmt),
                t.getTransactionType(),
                t.getAmount(),
                t.getDescription() != null ? t.getDescription() : ""))
                .collect(Collectors.joining("\n"));
    }
}
