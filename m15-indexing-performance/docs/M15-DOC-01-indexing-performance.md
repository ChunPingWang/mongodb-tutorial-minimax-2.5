# M15: 索引策略與效能調優

## 學習目標

設計有效的索引策略，優化查詢效能。

## 索引類型

| 類型 | 說明 |
|------|------|
| Single Field Index | 單一欄位索引 |
| Compound Index | 複合索引 |
| Text Index | 全文檢索索引 |
| Geospatial Index | 地理空間索引 |
| Hashed Index | 雜湊索引 |
| Partial Index | 部分索引 |
| TTL Index | 自動過期索引 |

## Compound Index 設計 - ESR 規則

### Equality, Sort, Range

```java
// 查詢: 找出某帳戶的某類型交易，按日期排序
db.transactions.find({
    accountNumber: "A001",
    type: "TRANSFER"
}).sort({ createdAt: -1 })

// 最佳索引: (accountNumber, type, createdAt)
// - Equality: accountNumber, type
// - Sort: createdAt
// - Range: 無
```

### 覆蓋查詢 (Covered Query)

```java
// 建立索引
db.accounts.createIndex(
    { accountNumber: 1, status: 1 },
    { name: "account_status_idx" }
)

// 查詢只回傳索引欄位，使用覆蓋查詢
db.accounts.find(
    { accountNumber: "A001", status: "ACTIVE" },
    { accountNumber: 1, status: 1, _id: 0 }
)
```

## 效能基準測試

```java
@Test
void testQueryPerformance() {
    // 建立測試資料
    for (int i = 0; i < 100000; i++) {
        mongoTemplate.insert(new Transaction(...));
    }
    
    // 測試查詢效能
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    
    List<Transaction> results = transactionRepository
        .findByAccountNumberAndDateRange("A001", startDate, endDate);
    
    stopWatch.stop();
    
    // 驗證查詢時間 < 100ms
    assertThat(stopWatch.getTotalTimeMillis()).isLessThan(100);
}
```
