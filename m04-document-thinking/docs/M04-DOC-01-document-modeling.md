# M04: Document 思維與基礎建模

## 學習目標

從 RDB 正規化思維轉換至 Document 建模思維，掌握 Embedding vs Referencing 決策原則。

## 1. Document 建模原則

### 1.1 核心原則

**「一起讀取的資料，一起儲存」**

這是 Document 資料庫最重要的設計原則。如果某些資料總是被一起查詢，應該考慮將它們嵌入在同一個文檔中。

### 1.2 Embedding vs Referencing 決策矩陣

| 關係類型 | 建議方式 | 考量點 |
|----------|----------|--------|
| 1:1 (強關聯) | Embedding | 資料是否經常一起讀取？ |
| 1:N (有限) | Embedding | N 是否有限且穩定？ |
| 1:N (無限) | Referencing | N 是否會無限增長？ |
| N:N | Referencing | 避免資料複製 |

### 1.3 Anti-Patterns

**過度嵌入**:
```javascript
// 不好: 過度嵌入
{
    "_id": ObjectId("..."),
    "customerName": "張三",
    "transactions": [
        // 數百萬筆交易不應嵌入
    ]
}
```

**無限增長的陣列**:
```javascript
// 不好: 交易記錄無限增長
{
    "_id": ObjectId("..."),
    "accountNumber": "A001",
    "transactions": [
        // 每天增加，最終導致文檔過大
    ]
}
```

**好的設計**:
```javascript
// 好: 引用外部集合
{
    "_id": ObjectId("..."),
    "customerName": "張三"
}

// 獨立的交易集合
{
    "_id": ObjectId("..."),
    "accountId": ObjectId("customer_id"),
    "transactions": [
        // 每個帳戶的交易獨立儲存
    ]
}
```

## 2. BSON 資料類型與 Java 映射

### 2.1 BSON Types → Java Types 對照表

| BSON Type | Java Type | 說明 |
|-----------|-----------|------|
| Double | Double | 64位浮點數 |
| String | String | UTF-8 字串 |
| Object | Document / POJO | 巢狀文檔 |
| Array | List | 陣列 |
| ObjectId | ObjectId | MongoDB 唯一識別 |
| Boolean | Boolean | true/false |
| Date | java.util.Date / Instant | 日期時間 |
| Integer | Integer | 32位整數 |
| Long | Long | 64位整數 |
| Decimal128 | BigDecimal | 高精度金額 |
| Binary | byte[] | 二進制資料 |

### 2.2 ObjectId 生成機制

```java
// MongoDB ObjectId
ObjectId id = new ObjectId();
// 或從字串解析
ObjectId id = new ObjectId("507f1f77bcf86cd799439011");
```

ObjectId 結構:
- 4 bytes: 時間戳記
- 3 bytes: 機器識別碼
- 2 bytes: 程序ID
- 3 bytes: 遞增計數器

### 2.3 Decimal128 在金融計算中的重要性

```java
// 錯誤: 使用 Double 會導致精度問題
class Account {
    private Double balance;  // 不建議
}

// 正確: 使用 BigDecimal
class Account {
    private BigDecimal balance;  // 推薦
}
```

**為什麼不用 Double?**:
```java
// Double 精度問題
System.out.println(0.1 + 0.2);  // 0.30000000000000004

// BigDecimal 精確計算
BigDecimal a = new BigDecimal("0.1");
BigDecimal b = new BigDecimal("0.2");
System.out.println(a.add(b));  // 0.3
```

## 3. 金融場景建模實戰

### 3.1 案例 1: 銀行客戶 Profile

**Embedding 策略** (適合讀取為主的場景):

```java
@Document(collection = "customers")
public class Customer {
    @Id
    private ObjectId id;
    
    private String name;
    private String email;
    
    @Field("contact_address")
    private Address contactAddress;
    
    @Field("registered_address")
    private Address registeredAddress;
    
    private KYCData kyc;
    
    // Getters and Setters
}

public record Address(
    String city,
    String district,
    String street,
    String zipCode
) {}

public record KYCData(
    LocalDateTime verifiedAt,
    String riskLevel,
    List<String> documents
) {}
```

**對應的 Document 結構**:
```javascript
{
    "_id": ObjectId("..."),
    "name": "張三",
    "email": "zhangsan@example.com",
    "contact_address": {
        "city": "台北市",
        "district": "信義區",
        "street": "松山路100號",
        "zipCode": "110"
    },
    "registered_address": {
        "city": "新北市",
        "district": "中和區",
        "street": "中和路200號",
        "zipCode": "235"
    },
    "kyc": {
        "verifiedAt": ISODate("2024-01-15T10:30:00Z"),
        "riskLevel": "LOW",
        "documents": ["id_card", "address_proof"]
    }
}
```

### 3.2 案例 2: 保險保單

**混合策略** (Embedding + Referencing):

```java
@Document(collection = "policies")
public class InsurancePolicy {
    @Id
    private ObjectId id;
    
    private String policyNumber;
    private String status;
    
    @Field("insured_person")
    private InsuredPerson insuredPerson;
    
    @Field("coverages")
    private List<Coverage> coverages;
    
    @Field("claim_history")
    private List<ObjectId> claimHistoryIds;  // 引用理賠記錄
    
    // Getters and Setters
}

public record InsuredPerson(
    String name,
    String idNumber,
    LocalDate birthDate,
    Address address
) {}

public record Coverage(
    String type,
    BigDecimal limit,
    BigDecimal deductible
) {}
```

### 3.3 案例 3: 電商商品目錄

**Referencing 策略** (需要獨立更新):

```java
@Document(collection = "products")
public class Product {
    @Id
    private ObjectId id;
    
    private String sku;
    private String name;
    private String description;
    
    @Field("specifications")
    private Map<String, String> specifications;
    
    @Field("variants")
    private List<ProductVariant> variants;
    
    // Getters and Setters
}

public record ProductVariant(
    String sku,
    String size,
    String color,
    BigDecimal price,
    Integer stock
) {}
```

## 4. Embedding vs Referencing 實驗

### 4.1 實驗設計

**方案 A: 帳戶嵌入客戶文檔**

```java
@Document(collection = "customers_with_accounts")
public class CustomerWithAccounts {
    @Id
    private ObjectId id;
    
    private String name;
    private List<BankAccount> accounts;
}
```

**方案 B: 帳戶獨立集合 + 引用**

```java
@Document(collection = "customers")
public class Customer {
    @Id
    private ObjectId id;
    private String name;
}

@Document(collection = "accounts")
public class BankAccount {
    @Id
    private ObjectId id;
    
    @Field("customer_id")
    private ObjectId customerId;
    
    private String accountNumber;
    private BigDecimal balance;
}
```

### 4.2 效能比較

| 操作 | 方案 A (嵌入) | 方案 B (引用) |
|------|--------------|--------------|
| 查詢客戶+帳戶 | 1 次查詢 | 2 次查詢或 $lookup |
| 更新帳戶餘額 | 需要更新整個客戶文檔 | 只更新帳戶文檔 |
| 獨立擴展 | 困難 | 容易 |
| 資料一致性 | 強 (同一文檔) | 弱 (需應用層保證) |

### 4.3 選擇原則

- **選擇 Embedding**:
  - 資料總是一起讀取
  - 資料不會無限增長
  - 需要強一致性

- **選擇 Referencing**:
  - 資料需要獨立更新
  - 資料會無限增長
  - 支援水準擴展
