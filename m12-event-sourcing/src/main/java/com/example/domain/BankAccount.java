package com.example.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class BankAccount {

    private String accountNumber;
    private String accountHolderName;
    private BigDecimal balance;
    private AccountStatus status;
    private List<DomainEvent> uncommittedEvents;
    private int version;

    public BankAccount() {
        this.uncommittedEvents = new ArrayList<>();
    }

    public BankAccount(String accountNumber, String accountHolderName, BigDecimal initialBalance) {
        this();
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = initialBalance;
        this.status = AccountStatus.ACTIVE;
        this.version = 1;

        AccountOpenedEvent event = new AccountOpenedEvent(
            accountHolderName, 
            accountNumber, 
            initialBalance
        );
        this.uncommittedEvents.add(event);
    }

    public void credit(BigDecimal amount, String description) {
        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Cannot credit to a non-active account");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Credit amount must be positive");
        }

        this.balance = this.balance.add(amount);
        this.version++;

        DomainEvent event = new DomainEvent.AccountCreditedEvent(
            accountNumber, 
            amount.toPlainString(), 
            description
        );
        this.uncommittedEvents.add(event);
    }

    public void debit(BigDecimal amount, String description) {
        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Cannot debit from a non-active account");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Debit amount must be positive");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }

        this.balance = this.balance.subtract(amount);
        this.version++;

        DomainEvent event = new DomainEvent.AccountDebitedEvent(
            accountNumber, 
            amount.toPlainString(), 
            description
        );
        this.uncommittedEvents.add(event);
    }

    public void close(String reason) {
        if (this.balance.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Cannot close account with non-zero balance");
        }

        this.status = AccountStatus.CLOSED;
        this.version++;

        DomainEvent event = new DomainEvent.AccountClosedEvent(accountNumber, reason);
        this.uncommittedEvents.add(event);
    }

    public void applyEvent(DomainEvent event) {
        switch (event) {
            case AccountOpenedEvent opened -> {
                this.accountNumber = opened.accountNumber();
                this.accountHolderName = opened.accountHolderName();
                this.balance = new BigDecimal(opened.initialBalance());
                this.status = AccountStatus.ACTIVE;
                this.version = opened.version();
            }
            case DomainEvent.AccountCreditedEvent credited -> {
                this.balance = this.balance.add(new BigDecimal(credited.amount()));
                this.version = credited.version();
            }
            case DomainEvent.AccountDebitedEvent debited -> {
                this.balance = this.balance.subtract(new BigDecimal(debited.amount()));
                this.version = debited.version();
            }
            case DomainEvent.AccountClosedEvent closed -> {
                this.status = AccountStatus.CLOSED;
                this.version = closed.version();
            }
        }
    }

    public List<DomainEvent> getUncommittedEvents() {
        return List.copyOf(uncommittedEvents);
    }

    public void markEventsCommitted() {
        this.uncommittedEvents.clear();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public int getVersion() {
        return version;
    }

    public enum AccountStatus {
        ACTIVE,
        CLOSED,
        FROZEN
    }
}
