package com.example.service;

import java.math.BigDecimal;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferService {
    private final MongoTemplate mongoTemplate;

    public TransferService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Transactional
    public void transfer(String fromAccount, String toAccount, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        BigDecimal fromBalance = deductBalance(fromAccount, amount);
        if (fromBalance == null) {
            throw new IllegalStateException("Insufficient funds or account not found: " + fromAccount);
        }

        addBalance(toAccount, amount);
    }

    private BigDecimal deductBalance(String accountNumber, BigDecimal amount) {
        Query query = new Query(Criteria.where("accountNumber").is(accountNumber));
        var account = mongoTemplate.findOne(query, Account.class, "accounts");

        if (account == null) {
            return null;
        }

        BigDecimal currentBalance = account.getBalance();
        if (currentBalance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }

        BigDecimal newBalance = currentBalance.subtract(amount);
        Update update = new Update().set("balance", newBalance);
        mongoTemplate.updateFirst(query, update, Account.class);

        return newBalance;
    }

    private void addBalance(String accountNumber, BigDecimal amount) {
        Query query = new Query(Criteria.where("accountNumber").is(accountNumber));
        var account = mongoTemplate.findOne(query, Account.class, "accounts");

        if (account == null) {
            throw new IllegalStateException("Account not found: " + accountNumber);
        }

        BigDecimal newBalance = account.getBalance().add(amount);
        Update update = new Update().set("balance", newBalance);
        mongoTemplate.updateFirst(query, update, Account.class);
    }

    public static class Account {
        private String id;
        private String accountNumber;
        private BigDecimal balance;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getAccountNumber() { return accountNumber; }
        public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
        public BigDecimal getBalance() { return balance; }
        public void setBalance(BigDecimal balance) { this.balance = balance; }
    }
}
