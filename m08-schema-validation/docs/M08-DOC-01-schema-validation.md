# M08: Schema Validation 與資料治理

## 學習目標

在 MongoDB 彈性 Schema 中建立適度的資料品質控制。

## JSON Schema Validation

### 基本 Validation 規則

```java
// 建立 Collection 並設定 Validator
Document validator = new Document(
    "$jsonSchema",
    new Document("bsonType", "object")
        .append("required", List.of("policyNumber", "premium", "status"))
        .append("properties", new Document()
            .append("policyNumber", new Document()
                .append("bsonType", "string")
                .append("description", "保單號碼"))
            .append("premium", new Document()
                .append("bsonType", "decimal")
                .append("minimum", 0)
                .append("description", "保費"))
            .append("status", new Document()
                .append("enum", List.of("PENDING", "ACTIVE", "EXPIRED", "CANCELLED"))
                .append("description", "保單狀態"))
        )
);

// 建立 Collection
mongoTemplate.createCollection(
    "insurance_policies",
    CollectionOptions.validator(validator)
        .validationLevel(ValidationLevel.STRICT)
        .validationAction(ValidationAction.ERROR)
);
```

### Validation Level

| 等級 | 說明 |
|------|------|
| off | 關閉驗證 |
| strict | 嚴格驗證 (預設) |
| moderate | 寬鬆驗證 (僅驗證已知欄位) |

### Validation Action

| 動作 | 說明 |
|------|------|
| error | 拒絕不符合的文件 |
| warn | 記錄警告但不拒絕 |

## 混合策略

### Schema-on-Write + Schema-on-Read

```java
// 核心欄位: Schema-on-Write
@Document(collection = "policies")
@Validated
public class InsurancePolicy {
    @NotBlank
    @Id
    private String policyNumber;
    
    @NotNull
    @DecimalMin("0")
    private BigDecimal premium;
    
    @NotNull
    private PolicyStatus status;
    
    // 擴展欄位: Schema-on-Read (可選)
    private Map<String, Object> extensions;
}
```

### 版本化 Schema

```java
@Document(collection = "products")
public class Product {
    @Id
    private ObjectId id;
    
    private String sku;
    private String name;
    
    @Field("_schemaVersion")
    private Integer schemaVersion = 2;
    
    // V1 欄位
    private BigDecimal price;
    
    // V2 新增欄位
    private BigDecimal discountPrice;
    private List<String> tags;
}
```
