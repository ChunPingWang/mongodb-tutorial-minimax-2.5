# M05: CRUD 操作與 Repository Pattern

## 學習目標

掌握 Spring Data MongoDB 的基本 CRUD 操作，理解 Repository 體系與 OOP/DDD 映射策略。

## 1. Spring Data MongoDB Repository 體系

### 1.1 Repository 層級

```
┌─────────────────────────────────────────┐
│         MongoRepository                │  ← 基本 CRUD
├─────────────────────────────────────────┤
│      CrudRepository                    │  ← 增強 CRUD
├─────────────────────────────────────────┤
│     PagingAndSortingRepository         │  ← 分頁排序
├─────────────────────────────────────────┤
│      ReactiveMongoRepository            │  ← 反應式
└─────────────────────────────────────────┘
```

### 1.2 基本 Repository 定義

```java
public interface BankAccountRepository 
    extends MongoRepository<BankAccount, String> {
    
    // Spring Data 自動實作
    // - save(entity)
    // - findById(id)
    // - existsById(id)
    // - findAll()
    // - delete(entity)
    // - count()
}
```

### 1.3 核心註解

| 註解 | 用途 |
|------|------|
| @Document | 標註類別對應 MongoDB Collection |
| @Id | 標註主鍵欄位 |
| @Field | 指定 MongoDB 欄位名稱 |
| @Indexed | 建立索引 |
| @CompoundIndex | 複合索引 |
| @TextIndex | 全文檢索索引 |

### 1.4 自訂 Repository

```java
// Fragment Interface
public interface BankAccountCustomRepository {
    List<BankAccount> findByCustomCriteria(Criteria criteria);
}

// Implementation
public class BankAccountCustomRepositoryImpl 
    implements BankAccountCustomRepository {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Override
    public List<BankAccount> findByCustomCriteria(Criteria criteria) {
        return mongoTemplate.find(
            Query.query(criteria), 
            BankAccount.class
        );
    }
}

// Combine
public interface BankAccountRepository 
    extends MongoRepository<BankAccount, String>, 
           BankAccountCustomRepository {
}
```

## 2. OOP 與 DDD 映射策略

### 2.1 Entity vs Value Object

**Entity** (有唯一識別):
```java
@Document(collection = "bank_accounts")
public class BankAccount {
    @Id
    private String id;
    
    private String accountNumber;
    private BigDecimal balance;
    private AccountStatus status;
    
    // Entity 有自己的行為
    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
    
    public void withdraw(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
        this.balance = this.balance.subtract(amount);
    }
}
```

**Value Object** (無識別，不變):
```java
public record Money(
    BigDecimal amount,
    String currency
) {
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new CurrencyMismatchException();
        }
        return new Money(
            this.amount.add(other.amount), 
            this.currency
        );
    }
}
```

### 2.2 Aggregate Root → Collection

```java
@Document(collection = "insurance_policies")
public class InsurancePolicy {  // Aggregate Root
    @Id
    private ObjectId id;
    
    private String policyNumber;
    private PolicyStatus status;
    
    private List<Coverage> coverages;  // Embedding
    
    // Aggregate Root 負責維護內部一致性
    public void addCoverage(Coverage coverage) {
        this.coverages.add(coverage);
        // 業務規則驗證
    }
    
    public void removeCoverage(String coverageType) {
        this.coverages.removeIf(c -> c.type().equals(coverageType));
    }
}
```

### 2.3 Java Record 作為 Value Object

```java
// Value Object 使用 Record (Java 16+)
public record Coverage(
    String type,
    BigDecimal limit,
    BigDecimal deductible
) {
    // 自動生成:
    // - constructor
    // - equals()/hashCode()
    // - toString()
    // - getters (type(), limit(), deductible())
}
```

### 2.4 @PersistenceCreator

```java
@Document(collection = "products")
public class Product {
    @Id
    private ObjectId id;
    
    private String name;
    private BigDecimal price;
    
    @PersistenceCreator
    public Product(
        @Nullable ObjectId id,
        String name, 
        BigDecimal price
    ) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}
```

## 3. 銀行帳戶 CRUD 實驗

### 3.1 BDD Feature

```gherkin
Feature: 銀行帳戶管理
  Scenario: 開立新帳戶
    Given 客戶 "張三" 已通過 KYC 驗證
    When 開立活期存款帳戶 初始餘額 10000 元
    Then 帳戶狀態為 ACTIVE
    And 帳戶餘額為 10000 元

  Scenario: 帳戶存款
    Given 帳戶 "A001" 餘額為 10000 元
    When 存入 5000 元
    Then 帳戶餘額為 15000 元

  Scenario: 帳戶提款
    Given 帳戶 "A001" 餘額為 10000 元
    When 提領 3000 元
    Then 帳戶餘額為 7000 元

  Scenario: 餘額不足提款失敗
    Given 帳戶 "A001" 餘額為 1000 元
    When 提領 2000 元
    Then 提款失敗並回傳餘額不足錯誤
    And 帳戶餘額維持 1000 元
```

### 3.2 實作

```java
@Service
public class BankAccountService {

    @Autowired
    private BankAccountRepository repository;

    public BankAccount createAccount(String accountNumber, BigDecimal initialBalance) {
        BankAccount account = new BankAccount();
        account.setAccountNumber(accountNumber);
        account.setBalance(initialBalance);
        account.setStatus(AccountStatus.ACTIVE);
        account.setCreatedAt(Instant.now());
        
        return repository.save(account);
    }

    public BankAccount deposit(String accountNumber, BigDecimal amount) {
        BankAccount account = repository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        
        account.deposit(amount);
        return repository.save(account);
    }

    public BankAccount withdraw(String accountNumber, BigDecimal amount) {
        BankAccount account = repository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        
        account.withdraw(amount);
        return repository.save(account);
    }
}
```
