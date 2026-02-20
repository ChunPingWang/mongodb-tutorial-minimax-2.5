package com.example.service;

import com.example.domain.Account;
import com.example.domain.Transaction;
import com.example.es.AccountEventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountEventStore eventStore;

    public AccountService(AccountEventStore eventStore) {
        this.eventStore = eventStore;
    }

    public Account createAccount(String accountNumber, String customerId, String accountType, BigDecimal initialBalance) {
        Account account = new Account(accountNumber, customerId, accountType, initialBalance);
        
        eventStore.saveEvent(account.getId(), "ACCOUNT_CREATED", account);
        log.info("Created account: {}", account.getAccountNumber());
        
        return account;
    }

    public Optional<Account> getAccount(String accountId) {
        return eventStore.getAccount(accountId);
    }

    public void deposit(String accountId, BigDecimal amount, String description) {
        Account account = eventStore.getAccount(accountId)
            .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        Transaction transaction = new Transaction(accountId, "DEPOSIT", amount, description);
        
        account.credit(amount);
        account.addTransaction(transaction.getId());
        
        eventStore.saveEvent(accountId, "DEPOSIT_EVENT", account);
        eventStore.saveTransaction(transaction);
        
        log.info("Deposited {} to account {}", amount, accountId);
    }

    public void withdraw(String accountId, BigDecimal amount, String description) {
        Account account = eventStore.getAccount(accountId)
            .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        Transaction transaction = new Transaction(accountId, "WITHDRAWAL", amount, description);
        
        account.debit(amount);
        account.addTransaction(transaction.getId());
        
        eventStore.saveEvent(accountId, "WITHDRAWAL_EVENT", account);
        eventStore.saveTransaction(transaction);
        
        log.info("Withdrew {} from account {}", amount, accountId);
    }
}
