package com.example.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.HashedIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "accounts")
@CompoundIndexes({
    @CompoundIndex(name = "branch_status_idx", def = "{'branchId': 1, 'status': 1}"),
    @CompoundIndex(name = "type_balance_idx", def = "{'accountType': 1, 'balance': -1}")
})
public class AccountIndex {

    @Id
    private String id;

    @Indexed
    private String accountNumber;

    @Indexed
    private String customerId;

    @Indexed
    private String branchId;

    private String accountType;

    private String status;

    private BigDecimal balance;

    @Indexed
    private String email;

    @Indexed
    private Instant createdAt;

    @Indexed(expireAfter = "30d")
    private Instant lastAccessedAt;

    @HashedIndex
    private String fingerprint;

    public AccountIndex() {
    }

    public AccountIndex(String accountNumber, String customerId, String branchId,
                       String accountType, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.branchId = branchId;
        this.accountType = accountType;
        this.balance = balance;
        this.status = "ACTIVE";
        this.createdAt = Instant.now();
        this.lastAccessedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(Instant lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }
}
