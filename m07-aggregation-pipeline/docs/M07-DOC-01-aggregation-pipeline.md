# M07: Aggregation Pipeline

## 學習目標

掌握 MongoDB Aggregation Framework 進行複雜資料分析。

## Aggregation Pipeline 概念

### 資料流經多個轉換階段

```java
Aggregation aggregation = Aggregation.newAggregation(
    match(Criteria.where("status").is("COMPLETED")),
    group("accountNumber")
        .sum("amount").as("totalAmount")
        .count().as("transactionCount"),
    project()
        .and("_id").as("accountNumber")
        .and("totalAmount").as("totalAmount")
        .and("transactionCount").as("transactionCount"),
    sort(Sort.by(Sort.Direction.DESC, "totalAmount"))
);
```

### 核心階段

| 階段 | 說明 | SQL 對應 |
|------|------|----------|
| $match | 篩選資料 | WHERE |
| $project | 選擇欄位 | SELECT |
| $group | 分組彙總 | GROUP BY |
| $sort | 排序 | ORDER BY |
| $limit | 限制數量 | LIMIT |
| $skip | 跳過資料 | OFFSET |
| $lookup | 關聯查詢 | JOIN |
| $unwind | 展開陣列 | - |
| $facet | 多維度統計 | - |

### Spring Data MongoDB Aggregation API

```java
@Service
public class ReportService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public MonthlyReport generateMonthlyReport(String accountNumber, int year, int month) {
        Instant startOfMonth = Instant.parse(year + "-" + String.format("%02d", month) + "-01T00:00:00Z");
        Instant endOfMonth = startOfMonth.plus(31, ChronoUnit.DAYS);

        TypedAggregation<Transaction> aggregation = Aggregation.newAggregation(
            Transaction.class,
            match(Criteria.where("accountNumber").is(accountNumber)
                .and("createdAt").gte(startOfMonth).lt(endOfMonth)),
            group("$type")
                .sum("amount").as("total")
                .count().as("count"),
            project()
                .and("_id").as("type")
                .and("total").as("totalAmount")
                .and("count").as("transactionCount")
        );

        AggregationResults<MonthlyReport> results = 
            mongoTemplate.aggregate(aggregation, MonthlyReport.class);
        
        return results.getUniqueMappedResult();
    }
}
```
