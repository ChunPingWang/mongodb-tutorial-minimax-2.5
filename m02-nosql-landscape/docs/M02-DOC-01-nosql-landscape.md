# M02: NoSQL 版圖 - MongoDB vs Redis vs Cassandra

## 學習目標

掌握不同 NoSQL 資料庫的定位與適用場景，學習 Polyglot Persistence 架構。

## 1. NoSQL 四大類型全景圖

### 1.1 Document Store - MongoDB

**特性**:
- 彈性結構 (Flexible Schema)
- Ad-hoc 查詢能力
- 豐富的索引支援
- 水平擴展 (Sharding)

**適用場景**:
- 內容管理系統
- 客戶資料管理
- 產品目錄
- 行動應用後端

### 1.2 Key-Value Store - Redis

**特性**:
- 記憶體儲存 (In-Memory)
- 極低延遲
- 豐富的資料結構
- 支援 Pub/Sub

**適用場景**:
- 快取 (Caching)
- Session 儲存
- 排行榜
- 訊息佇列
- 即時計數器

### 1.3 Wide-Column Store - Cassandra

**特性**:
- 高寫入吞吐量
- 線性擴展
- 無主節點架構
- 最佳化寫入

**適用場景**:
- IoT 時序資料
- 交易日誌
- 訊息記錄
- 大規模分析

### 1.4 Graph Database - Neo4j

**特性**:
- 節點和邊關係
- 高效關係遍歷
- 圖Traversal查詢

**適用場景**:
- 社群網路
- 推薦引擎
- 欺詐偵測
- 網路拓撲

## 2. MongoDB vs Redis vs Cassandra 深度比較

| 維度 | MongoDB | Redis | Cassandra |
|------|---------|-------|-----------|
| 資料模型 | Document (BSON) | Key-Value / 資料結構 | Wide-Column |
| 查詢能力 | 豐富 (Ad-hoc) | 簡單 (Key Lookup) | CQL (類SQL) |
| 一致性 | 可調 | 強一致/最終一致 | 可調 |
| 擴展方式 | Sharding | Hash Slot | Consistent Hashing |
| 寫入效能 | 中高 | 極高 | 極高 |
| 記憶體 | 磁碟為主 | 記憶體優先 | 磁碟為主 |
| 交易支援 | 多文件 ACID | Lua Script | 輕量級 Batch |
| 適用場景 | 通用文件 | 快取/計數器 | 時序/日誌 |

## 3. 金融場景 Polyglot Persistence

### 3.1 銀行系統架構

```
┌─────────────────────────────────────────────────────────────┐
│                      API Gateway                             │
└─────────────────────────────────────────────────────────────┘
         │              │              │             │
         ▼              ▼              ▼             ▼
┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│   Core      │  │   CRM       │  │  Risk       │  │  Trading    │
│   Banking   │  │   System    │  │  Control    │  │  Log        │
│   (RDB)     │  │  (MongoDB)  │  │  (Redis)    │  │(Cassandra)  │
└─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘
```

**選型理由**:
- **Core Banking (RDB)**: 需要複雜查詢與強一致性
- **CRM (MongoDB)**: 客戶資料多樣，需要彈性結構
- **Risk Control (Redis)**: 需要極低延遲的風險計算
- **Trading Log (Cassandra)**: 高寫入量的交易記錄

### 3.2 電商系統架構

```
┌─────────────────────────────────────────────────────────────┐
│                      API Gateway                             │
└─────────────────────────────────────────────────────────────┘
         │              │              │             │
         ▼              ▼              ▼             ▼
┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│   Order     │  │   Product   │  │   Cart     │  │  Analytics  │
│   Service   │  │   Catalog   │  │  Service   │  │  Service    │
│   (RDB)     │  │  (MongoDB)  │  │  (Redis)   │  │(Cassandra)  │
└─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘
```

## 4. 實驗: 三種 NoSQL 併行測試

### 4.1 Testcontainers 設定

```java
@Container
static MongoDBContainer mongoDB = new MongoDBContainer("mongo:7.0");

@Container
static GenericContainer<?> redis = new GenericContainer("redis:7");

@Container
static CassandraContainer cassandra = new CassandraContainer("cassandra:4");
```

### 4.2 測試場景: 電商訂單處理

```gherkin
Feature: 電商訂單資料存取
  Scenario: 訂單建立後分散式儲存
    Given 訂單 "O001" 包含:
      | 欄位 | 值 |
      | 客戶ID | C001 |
      | 總金額 | 5000 |
      | 狀態 | CREATED |
    When 訂單建立事件發生
    Then 訂單主檔存儲至 MongoDB
    And 訂單狀態存儲至 Redis (快速查詢)
    And 訂單歷史存儲至 Cassandra (日誌分析)
```

## 5. 整合實驗: Redis 快取 + MongoDB 持久化

### 5.1 Cache-Aside Pattern

```
1. 查詢請求到達
2. 檢查 Redis 快取
   ├─ 命中 → 直接回傳
   └─ 未命中 →
       ├─ 查詢 MongoDB
       ├─ 結果存入 Redis
       └─ 回傳結果
```

### 5.2 實作範例

```java
@Service
public class ProductService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ProductRepository productRepository;

    public Product findById(String id) {
        // 1. 查詢快取
        String cacheKey = "product:" + id;
        Product cached = (Product) redisTemplate.opsForValue().get(cacheKey);
        
        if (cached != null) {
            return cached;
        }

        // 2. 查詢資料庫
        Product product = productRepository.findById(id).orElseThrow();

        // 3. 寫入快取 (TTL: 1小時)
        redisTemplate.opsForValue().set(cacheKey, product, 1, TimeUnit.HOURS);

        return product;
    }
}
```
