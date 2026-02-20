package com.example.service;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import com.example.domain.BankAccount;
import com.example.repository.BankAccountRepository;

@Service
public class BankAccountService {

    private final BankAccountRepository repository;

    public BankAccountService(BankAccountRepository repository) {
        this.repository = repository;
    }

    public BankAccount createAccount(String accountNumber, BigDecimal initialBalance) {
        BankAccount account = new BankAccount(accountNumber, initialBalance);
        return repository.save(account);
    }

    public BankAccount deposit(String accountNumber, BigDecimal amount) {
        BankAccount account = repository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new RuntimeException("帳戶不存在: " + accountNumber));
        account.deposit(amount);
        return repository.save(account);
    }

    public BankAccount withdraw(String accountNumber, BigDecimal amount) {
        BankAccount account = repository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new RuntimeException("帳戶不存在: " + accountNumber));
        account.withdraw(amount);
        return repository.save(account);
    }

    public BankAccount findByAccountNumber(String accountNumber) {
        return repository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new RuntimeException("帳戶不存在: " + accountNumber));
    }
}
