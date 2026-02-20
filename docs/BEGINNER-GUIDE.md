# MongoDB 初學者指南

## 什麼是 MongoDB？

MongoDB 是一個基於文件（Document）的 NoSQL 資料庫，使用 JSON-like 的 BSON 格式存儲資料。與傳統的關聯式資料庫（RDB）不同，MongoDB 提供了更靈活的資料模型和水準擴展能力。

## 為什麼選擇 MongoDB？

### 優勢

1. **靈活的 Schema**: 不需要預先定義資料結構
2. **水準擴展**: 支援分片（Sharding）實現資料分散儲存
3. **豐富的查詢功能**: 支持複雜的 Ad-hoc 查詢
4. **高性能**: 針對讀寫操作進行優化
5. **開發效率**: 使用熟悉的 JSON/JS 語法

### 適用場景

- 內容管理系統（CMS）
- 產品目錄與庫存管理
- 用戶個人資料與偏好設置
- 物聯網（IoT）資料存儲
- 行動應用後端
- 即時分析

### 不適用場景

- 需要複雜多表 JOIN 的業務
- 需要強一致性（銀行轉帳等）
- 高度正規化的資料結構

## MongoDB 核心概念

### Collection（集合）

相當於 RDB 的「表」（Table）。Collection 是存放 Document 的容器。

```javascript
// users Collection
{
  "name": "張三",
  "age": 30
}
```

### Document（文檔）

相當於 RDB 的「行」（Row）。Document 是 MongoDB 的基本資料單位。

```javascript
{
  "_id": ObjectId("..."),
  "name": "張三",
  "email": "zhangsan@example.com",
  "address": {
    "city": "台北市",
    "district": "信義區"
  },
  "tags": ["vip", "premium"]
}
```

### Field（欄位）

Document 中的鍵值對，相當於 RDB 的「列」（Column）。

## Document 模型設計

### 基本原則

**「一起讀取的資料，一起儲存」**

這是 Document 資料庫最重要的設計原則。

### Embedding（嵌入）vs Referencing（引用）

#### Embedding（嵌入）

將相關資料直接儲存在主 Document 中：

```javascript
{
  "customer": "張三",
  "orders": [
    { "orderId": "O001", "total": 1000 },
    { "orderId": "O002", "total": 2000 }
  ]
}
```

**適用時機**:
- 資料總是一起讀取
- 資料不會無限增長
- 需要強一致性

#### Referencing（引用）

將相關資料儲存在不同 Collection，透過 ID 關聯：

```javascript
// customers Collection
{
  "_id": ObjectId("c1"),
  "name": "張三"
}

// orders Collection  
{
  "_id": ObjectId("o1"),
  "customerId": ObjectId("c1"),
  "total": 1000
}
```

**適用時機**:
- 資料需要獨立更新
- 資料會無限增長
- 需要水準擴展

## MongoDB 與 Java Spring 整合

### Spring Data MongoDB

Spring Data MongoDB 提供了簡化的資料訪問層：

```java
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    
    // getters/setters
}

public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByName(String name);
    List<User> findByEmailContaining(String keyword);
}
```

### 基本 CRUD 操作

```java
@Autowired
private UserRepository userRepository;

// Create
User user = new User("張三", "zhangsan@example.com");
userRepository.save(user);

// Read
Optional<User> found = userRepository.findById(id);
List<User> users = userRepository.findByName("張三");

// Update
user.setEmail("new@example.com");
userRepository.save(user);

// Delete
userRepository.deleteById(id);
```

## 常見術語對照

| RDB 術語 | MongoDB 術語 |
|----------|--------------|
| Database | Database |
| Table | Collection |
| Row | Document |
| Column | Field |
| Primary Key | _id |
| JOIN | $lookup / Embedding |
| WHERE | $match |
| GROUP BY | $group |
| ORDER BY | $sort |

## 下一步

1. **開始學習 M01**: [RDB vs NoSQL 思維轉換](m01-rdb-vs-nosql/)
2. **設置環境**: [開發環境與測試基礎設施](m03-environment-setup/)
3. **動手實作**: [CRUD 操作與 Repository Pattern](m05-spring-data-crud/)

## 資源

- [MongoDB 官方網站](https://www.mongodb.com/)
- [MongoDB University](https://university.mongodb.com/)
- [Spring Data MongoDB 文檔](https://spring.io/projects/spring-data-mongodb)
