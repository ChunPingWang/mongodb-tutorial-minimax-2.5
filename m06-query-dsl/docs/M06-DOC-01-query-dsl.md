# M06: 查詢 DSL 與方法名稱推導

## 學習目標

精通 Spring Data MongoDB 的多種查詢方式：Method Name Derivation、@Query、Criteria API、MongoTemplate。

## 查詢方法四層體系

### Level 1: Method Name Derivation

```java
public interface BankAccountRepository 
    extends MongoRepository<BankAccount, String> {
    
    // Spring Data 自動解析方法名稱
    List<BankAccount> findByStatus(AccountStatus status);
    
    List<BankAccount> findByBalanceGreaterThan(BigDecimal amount);
    
    List<BankAccount> findByStatusAndBalanceGreaterThan(
        AccountStatus status, 
        BigDecimal amount
    );
    
    Optional<BankAccount> findByAccountNumber(String accountNumber);
    
    long countByStatus(AccountStatus status);
    
    boolean existsByAccountNumber(String accountNumber);
    
    void deleteByStatus(AccountStatus status);
}
```

### Level 2: @Query 手寫 JSON

```java
public interface TransactionRepository 
    extends MongoRepository<Transaction, String> {
    
    @Query("{ 'accountNumber': ?0, 'createdAt': { $gte: ?1, $lte: ?2 } }")
    List<Transaction> findByAccountAndDateRange(
        String accountNumber, 
        Instant start, 
        Instant end
    );
    
    @Query("{ 'amount': { $gt: ?0 }, 'type': ?1 }")
    List<Transaction> findByAmountGreaterThanAndType(
        BigDecimal amount, 
        String type
    );
    
    @Query("{ $text: { $search: ?0 } }")
    List<Transaction> textSearch(String searchText);
}
```

### Level 3: Criteria API

```java
@Service
public class TransactionQueryService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Transaction> findByDynamicCriteria(
        String accountNumber,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        List<String> types
    ) {
        Criteria criteria = new Criteria();
        
        List<Criteria> conditions = new ArrayList<>();
        
        if (accountNumber != null) {
            conditions.add(Criteria.where("accountNumber").is(accountNumber));
        }
        
        if (minAmount != null || maxAmount != null) {
            Criteria amountCriteria = Criteria.where("amount");
            if (minAmount != null) amountCriteria.gte(minAmount);
            if (maxAmount != null) amountCriteria.lte(maxAmount);
            conditions.add(amountCriteria);
        }
        
        if (types != null && !types.isEmpty()) {
            conditions.add(Criteria.where("type").in(types));
        }
        
        if (!conditions.isEmpty()) {
            criteria.andOperator(conditions.toArray(new Criteria[0]));
        }
        
        return mongoTemplate.find(
            Query.query(criteria), 
            Transaction.class
        );
    }
}
```

### Level 4: MongoTemplate 完全控制

```java
@Service
public class AdvancedQueryService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Page<Transaction> findWithPagination(
        String accountNumber,
        int page,
        int size,
        String sortBy,
        boolean ascending
    ) {
        Query query = new Query();
        query.addCriteria(Criteria.where("accountNumber").is(accountNumber));
        
        long total = mongoTemplate.count(query, Transaction.class);
        
        query.with(PageRequest.of(page, size));
        query.with(Sort.by(
            ascending ? Sort.Direction.ASC : Sort.Direction.DESC, 
            sortBy
        ));
        
        List<Transaction> results = mongoTemplate.find(query, Transaction.class);
        
        return new PageImpl<>(results, PageRequest.of(page, size), total);
    }
}
```

## 複雜查詢模式

### 巢狀文檔查詢

```java
// 查詢巢狀地址
List<Customer> findByAddressCity(String city);

// 使用 Criteria
query.addCriteria(Criteria.where("address.city").is(city));
```

### 陣列查詢

```java
// $elemMatch - 查詢陣列中符合條件的元素
@Query("{ 'coverages': { $elemMatch: { 'type': ?0 } } }")
List<InsurancePolicy> findByCoverageType(String type);

// $all - 包含所有指定元素
@Query("{ 'tags': { $all: ?0 } }")
List<Product> findByAllTags(List<String> tags);

// $in - 任一匹配
@Query("{ 'status': { $in: ?0 } }")
List<Order> findByStatusIn(List<String> statuses);
```

### 地理空間查詢

```java
@Query("{ 'location': { $near: { $geometry: { type: 'Point', coordinates: [?0, ?1] }, $maxDistance: ?2 } } }")
List<Branch> findNearbyBranches(double lng, double lat, int maxDistanceMeters);
```

### 全文搜尋

```java
@Query("{ $text: { $search: ?0 } }")
@TextScore
List<Product> findByTextSearch(String searchText);
```
