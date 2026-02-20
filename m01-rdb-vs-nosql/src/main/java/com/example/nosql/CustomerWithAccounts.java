package com.example.nosql;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class CustomerWithAccounts {
    private String id;
    private String name;
    private String email;
    private List<BankAccount> accounts;
    private Instant createdAt;

    public CustomerWithAccounts() {}

    public CustomerWithAccounts(String name, String email) {
        this.name = name;
        this.email = email;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<BankAccount> getAccounts() { return accounts; }
    public void setAccounts(List<BankAccount> accounts) { this.accounts = accounts; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static class BankAccount {
        private String accountNumber;
        private BigDecimal balance;
        private String status;
        private Instant createdAt;

        public BankAccount() {}

        public BankAccount(String accountNumber, BigDecimal balance, String status) {
            this.accountNumber = accountNumber;
            this.balance = balance;
            this.status = status;
            this.createdAt = Instant.now();
        }

        public String getAccountNumber() { return accountNumber; }
        public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
        public BigDecimal getBalance() { return balance; }
        public void setBalance(BigDecimal balance) { this.balance = balance; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    }
}
