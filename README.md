# MongoDB for Java Spring Developers

![Java](https://img.shields.io/badge/Java-23-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![MongoDB](https://img.shields.io/badge/MongoDB-7.0-brightgreen)
![Gradle](https://img.shields.io/badge/Gradle-8.11-blue)

> 完整的 MongoDB + Spring Boot 教學課程，透過情境驅動學習金融、保險、電商應用場景。

## 專案簡介

本專案提供一套完整的 MongoDB 開發者培訓課程，專為具備 RDB 背景的 Java Spring 開發人員設計。採用 Test-First (BDD/TDD) 教學法，讓學習者透過實際動手做來掌握 MongoDB 核心技術。

## 技術棧

| 技術 | 版本 |
|------|------|
| Java | 23 |
| Spring Boot | 3.4.0 |
| Gradle | 8.11 |
| MongoDB | 7.0 |
| Testcontainers | 1.20.0 |

## 學習路徑

### Phase 1: 基礎建設與思維轉換 ⭐

| Module | 主題 | 說明 |
|--------|------|------|
| M01 | RDB vs NoSQL 思維轉換 | 理解關聯式與文件資料庫的設計差異 |
| M02 | NoSQL 版圖 | MongoDB vs Redis vs Cassandra 比較 |
| M03 | 開發環境與測試基礎設施 | Testcontainers + BDD 測試環境 |
| M04 | Document 思維與基礎建模 | Embedding vs Referencing 決策 |

### Phase 2: Spring Data MongoDB 核心 ⭐⭐

| Module | 主題 | 說明 |
|--------|------|------|
| M05 | CRUD 操作與 Repository Pattern | Spring Data MongoDB 基本操作 |
| M06 | 查詢 DSL 與方法名稱推導 | @Query, Criteria API, MongoTemplate |
| M07 | Aggregation Pipeline | 複雜資料分析與彙總查詢 |
| M08 | Schema Validation 與資料治理 | JSON Schema 驗證與資料品質控制 |
| M09 | Multi-Document Transactions | 分散式交易與 SAGA Pattern 入門 |

### Phase 3: 領域驅動與進階建模 ⭐⭐⭐

| Module | 主題 | 說明 |
|--------|------|------|
| M10 | DDD Aggregate 建模 | Aggregate Root 與 Collection 映射 |
| M11 | 多型與繼承建模 | Sealed Interface + MongoDB |
| M12 | Event Sourcing with MongoDB | 事件溯源模式 |
| M13 | CQRS Read Model | Command/Query 分離架構 |
| M14 | SAGA Pattern | 跨 Aggregate 分散式交易 |

### Phase 4: 效能、可觀測性與維運 ⭐⭐⭐

| Module | 主題 | 說明 |
|--------|------|------|
| M15 | 索引策略與效能調優 | 索引設計與效能優化 |
| M16 | Change Streams 與事件驅動 | 即時資料同步 |
| M17 | 可觀測性與監控 | Metrics + Logging + Tracing |
| M18 | Schema Migration 與版本管理 | 資料遷移策略 |

### Phase 5: 整合專案 (Capstone) ⭐⭐⭐

| Module | 主題 | 說明 |
|--------|------|------|
| M19 | 銀行核心系統 | 完整帳戶管理系統 |
| M20 | 保險理賠系統 | 多險種理賠處理 |
| M21 | 電商平台 | 高併發訂單處理 |

## 快速開始

### 前置需求

- Java 23+
- Docker Desktop (用於 Testcontainers)
- Git

### 克隆專案

```bash
git clone https://github.com/ChunPingWang/mongodb-tutorial-minimax-2.5.git
cd mongodb-tutorial-minimax-2.5
```

### 建置專案

```bash
# 建置所有模組
./gradlew build

# 建置特定模組
./gradlew :m05-spring-data-crud:build
```

### 執行測試

```bash
# 執行所有測試
./gradlew test

# 執行特定模組測試
./gradlew :m05-spring-data-crud:test
```

## 專案結構

```
mongodb-spring-course/
├── build.gradle.kts              # Root build 配置
├── settings.gradle.kts           # 模組設定
├── gradle/
│   └── libs.versions.toml       # 版本目錄
├── buildSrc/                    # Convention Plugin
├── m01-rdb-vs-nosql/           # Module 1
│   ├── build.gradle.kts
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   │       ├── java/
│   │       └── resources/
│   ├── docs/                   # 教學文件
│   └── README.md
├── m02-nosql-landscape/
│   └── ...
└── m21-ecommerce-capstone/
```

## 學習建議

### 對於初學者

1. **從 M01 開始**: 理解 RDB 與 NoSQL 的根本差異
2. **跟著文件學習**: 每個 Module 的 `docs/` 目錄包含詳細說明
3. **動手實作**: 嘗試修改程式碼並執行測試
4. **建立 Mental Model**: 理解 Document 思維而非套用 RDB 觀念

### 核心概念

- **Document Model**: 「一起讀取的資料，一起儲存」
- **Schema-on-Read**: 寫入彈性，讀取時解釋結構
- **Embedding vs Referencing**: 依查詢模式選擇
- **Aggregate Boundary**: Transaction 邊界 = Document 邊界

## 常見問題

### Q: 為什麼選擇 MongoDB 而非傳統 RDB？

A: 當資料結構多變、需要快速開發迭代、或需要水準擴展能力時，MongoDB 是很好的選擇。但對於需要強一致性的金融交易場景，仍建議使用 RDB 或混合架構。

### Q: 如何決定 Embedding 還是 Referencing？

A: 遵循原則：「一起讀取的資料，一起儲存」。如果某資料總是被一起查詢，考慮 Embedding；如果某資料會獨立更新或無限增長，考慮 Referencing。

### Q: MongoDB 支援事務嗎？

A: 支援多文件 ACID 交易 (MongoDB 4.0+)，但需要 Replica Set。建議優先考慮最終一致性設計，或使用 SAGA Pattern。

## 延伸閱讀

- [MongoDB 官方文檔](https://docs.mongodb.com/)
- [Spring Data MongoDB 參考](https://spring.io/projects/spring-data-mongodb)
- [Martin Fowler - Event Sourcing](https://martinfowler.com/eaaDev/EventSourcing.html)
- [Microsoft - CQRS](https://docs.microsoft.com/en-us/azure/architecture/patterns/cqrs)

## 授權

本專案僅供內部教育訓練使用。

---

**作者**: ChunPing Wang  
**更新日期**: 2026-02-20
