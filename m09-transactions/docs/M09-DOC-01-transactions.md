# M09: Multi-Document Transactions

## 學習目標

理解 MongoDB Transaction 的能力與限制。

## Transaction 深度解析

### 基本 Transaction 使用

```java
@Service
public class TransferService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Transactional
    public void transfer(String fromAccount, String toAccount, BigDecimal amount) {
        // 扣款
        Query fromQuery = Query.query(
            Criteria.where("accountNumber").is(fromAccount)
                .and("balance").gte(amount)
        );
        Update fromUpdate = new Update().inc("balance", amount.negate());
        UpdateResult fromResult = mongoTemplate.updateFirst(fromQuery, fromUpdate, BankAccount.class);
        
        if (fromResult.getMatchedCount() == 0) {
            throw new InsufficientBalanceException("餘額不足");
        }
        
        // 存款
        Query toQuery = Query.query(Criteria.where("accountNumber").is(toAccount));
        Update toUpdate = new Update().inc("balance", amount);
        mongoTemplate.updateFirst(toQuery, toUpdate, BankAccount.class);
        
        // 記錄交易
        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(amount);
        transaction.setCreatedAt(Instant.now());
        mongoTemplate.save(transaction);
    }
}
```

### TransactionManager 設定

```java
@Configuration
public class MongoConfig {

    @Bean
    public MongoTransactionManager mongoTransactionManager(MongoDbFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}
```

### Write Concern / Read Concern

```java
// Write Concern
mongoTemplate.setWriteConcern(WriteConcern.WEARED_MAJORITY);
mongoTemplate.setWriteConcern(WriteConcern.JOURNALED);

// Read Concern
mongoTemplate.setReadPreference(ReadPreference.secondary());
mongoTemplate.setReadConcern(ReadConcern.MAJORITY);
```

## 與 RDB Transaction 比較

### 能力

| 特性 | MongoDB Transaction | RDB Transaction |
|------|-------------------|-----------------|
| ACID | 支援 | 支援 |
| 多文件 | 支援 | 支援 |
| 多集合 | 支援 | 支援 |
| 分散式 | 支援 (4.2+) | 支援 |

### 限制

- 需要 Replica Set (單節點不支援)
- 效能開銷較大
- 不建議在高併發場景使用
- 建議使用 SAGA Pattern 替代

## SAGA Pattern 替代方案

```java
@Service
public class TransferSagaService {

    public void transfer(String fromAccount, String toAccount, BigDecimal amount) {
        // 1. 執行扣款 (Compensable)
        try {
            withdraw(fromAccount, amount);
        } catch (Exception e) {
            throw new TransferFailedException("扣款失敗", e);
        }
        
        // 2. 執行存款
        try {
            deposit(toAccount, amount);
        } catch (Exception e) {
            // 3. 補償: 回補扣款
            compensateWithdraw(fromAccount, amount);
            throw new TransferFailedException("轉帳失敗，已回滾", e);
        }
        
        // 4. 記錄交易 (成功)
        recordTransaction(fromAccount, toAccount, amount);
    }
}
```
