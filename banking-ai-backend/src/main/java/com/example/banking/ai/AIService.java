package com.example.banking.ai;

import com.example.banking.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIService {
    private final ChatClient.Builder chatClientBuilder;
    private final BankingTools bankingTools;

    private static final String SYSTEM_PROMPT = """
            You are a helpful banking assistant for authenticated customers.
            
            You can help with:
            - Account balance
            - Account details
            - Recent transactions
            - Account statements for a date range
            
            Rules:
            - When the customer asks for account information, ALWAYS use the appropriate banking tool.
            - Never invent or guess balances, transactions or account numbers.
            - Never request or expose another customer's information.
            - Never execute SQL or ask for customer ID.
            - Use only information returned by the banking tools.
            - Currency is INR (₹).
            - For simple questions give a short clear response.
            - For transaction lists format them readably, one per line.
            
            Examples:
            User: "What is my balance?"
            → Call getBalance tool, then reply: "Your current account balance is ₹45,250.00."
            
            User: "Show my last 3 transactions"
            → Call getRecentTransactions with limit=3, then format the list.
            """;

    public ChatResponse chat(String userMessage) {
        log.info("AI chat request received (message length={})",
                userMessage != null ? userMessage.length() : 0);
        try {
            ChatClient chatClient = chatClientBuilder
                    .defaultSystem(SYSTEM_PROMPT)
                    .defaultTools(bankingTools)
                    .build();
            String response = chatClient.prompt().user(userMessage).call().content();
            return ChatResponse.builder()
                    .message(response != null ? response : "I could not process your request.")
                    .build();
        } catch (Exception e) {
            log.error("AI service error: {}", e.getMessage());
            return ChatResponse.builder()
                    .message("Sorry, I am temporarily unable to process your request. Please try again later.")
                    .build();
        }
    }
}
