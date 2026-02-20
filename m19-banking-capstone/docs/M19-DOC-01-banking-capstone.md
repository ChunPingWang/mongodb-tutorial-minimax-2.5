# M19: 銀行核心系統 Capstone

## 模組簡介

本模組整合所有概念，建構完整的銀行帳戶管理系統。

## 專案架構

### Bounded Context Map

```
┌─────────────────────────────────────────────────────────────┐
│                    帳戶管理 Context                          │
│  ┌─────────────────┐    ┌─────────────────┐               │
│  │ BankAccount     │    │ Transaction     │               │
│  │ Aggregate       │    │ Aggregate       │               │
│  └─────────────────┘    └─────────────────┘               │
└─────────────────────────────────────────────────────────────┘
         │                          │
         ▼                          ▼
┌─────────────────────────────────────────────────────────────┐
│                    客戶管理 Context                          │
│  ┌─────────────────┐                                       │
│  │ Customer        │                                       │
│  │ Aggregate       │                                       │
│  └─────────────────┘                                       │
└─────────────────────────────────────────────────────────────┘
```

### 技術實現

- **Hexagonal Architecture**: 完整的 Port/Adapter 架構
- **Event Sourcing**: 帳戶交易事件溯源
- **CQRS**: 客戶 360 度視圖
- **SAGA**: 跨帳戶轉帳
- **Change Streams**: 即時餘額更新

## 實作內容

### 1. 帳戶 Aggregate

```java
@Document(collection = "accounts")
public class BankAccount implements AggregateRoot {
    @Id
    private ObjectId id;
    private String accountNumber;
    private BigDecimal balance;
    private AccountStatus status;
    private List<DomainEvent> domainEvents = new ArrayList<>();
    
    // 業務行為
    public void deposit(BigDecimal amount) {...}
    public void withdraw(BigDecimal amount) {...}
}
```

### 2. Event Store

```java
@Document(collection = "account_events")
public class AccountEvent {
    private String aggregateId;
    private int version;
    private String eventType;
    private Document payload;
    private Instant timestamp;
}
```

### 3. CQRS Read Model

```java
@Document(collection = "account_views")
public class AccountView {
    private String accountId;
    private String accountNumber;
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private Instant lastTransactionTime;
}
```

## 測試場景

```gherkin
Feature: 銀行轉帳
  Scenario: 跨帳戶轉帳
    Given 帳戶 A001 餘額 50000 元
    And 帳戶 A002 餘額 10000 元
    When 從 A001 轉帳 20000 元至 A002
    Then A001 餘額為 30000 元
    And A002 餘額為 30000 元
    And 產生轉帳事件記錄
```
