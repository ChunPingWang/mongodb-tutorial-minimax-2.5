# M01: RDB vs NoSQL 思維轉換

## 模組簡介

本模組帶領 Java 開發人員從關聯式資料庫 (RDB) 思維轉換至文件資料庫 (MongoDB) 思維，理解兩種資料庫的本質差異與適用場景。

## 學習目標

- 理解 RDB 與 NoSQL 的設計哲學差異
- 掌握 CAP 定理與一致性模型
- 建立正確的資料庫選型思維

## 內容大綱

### 文件 (DOC)

- **M01-DOC-01**: Data Model 哲學比較
- **M01-DOC-02**: CAP 定理與一致性模型
- **M01-DOC-03**: 選型決策框架

### 實驗室 (LAB)

- **M01-LAB-01**: RDB vs MongoDB 比較測試
- **M01-LAB-02**: Schema Evolution 對比實驗

## 快速開始

```bash
# 建置專案
./gradlew build

# 執行測試
./gradlew :m01-rdb-vs-nosql:test
```

## 技術堆疊

- Java 23
- Spring Boot 3.4.0
- Testcontainers
- PostgreSQL / MongoDB
