# MongoDB 術語辭典

本文件收錄 MongoDB 開發中常見的術語，幫助初學者快速理解。

## A

### ACID

Atomicity, Consistency, Isolation, Durability。資料庫交易的四個基本特性。MongoDB 在 Replica Set 環境下支援多文件 ACID 交易。

### Aggregation Pipeline

MongoDB 的資料處理框架，將資料透過多個階段（Stage）進行轉換和分析。

```javascript
db.orders.aggregate([
  { $match: { status: "completed" } },
  { $group: { _id: "$customerId", total: { $sum: "$amount" } } },
  { $sort: { total: -1 } }
])
```

## B

### BSON

Binary JSON。MongoDB 使用的二進制資料格式，比 JSON 更緊湊，支援更多資料類型（如 Date, ObjectId, Binary）。

### Bounded Context

領域驅動設計（DDD）中的概念，指一個特定的問題域邊界。

## C

### CAP Theorem

分散式資料系統無法同時滿足 Consistency（一致性）、Availability（可用性）和 Partition Tolerance（分區容錯）。必須在 CA、CP、AP 之間選擇。

### Change Streams

MongoDB 4.0+ 提供的功能，可以監聽 Collection 的變更事件，即時響應資料變化。

### Collection

MongoDB 中存放 Document 的容器，相當於關聯式資料庫的 Table。

### Compound Index

複合索引，在多個欄位上建立的索引。

```javascript
db.users.createIndex({ name: 1, age: -1 })
```

### Covered Query

覆蓋查詢，查詢只涉及索引欄位，無需讀取實際 Document。

## D

### DDD (Domain-Driven Design)

領域驅動設計。一種軟體開發方法論，強調以業務領域為核心進行建模。

### Document

MongoDB 中的基本資料單位，相當於關聯式資料庫的一行。是一個 JSON-like 的 BSON 物件。

```javascript
{
  "_id": ObjectId("..."),
  "name": "張三",
  "age": 30
}
```

## E

### Embedding

將相關資料嵌入在同一個 Document 中，適合「一起讀取」的場景。

### Event Sourcing

一種架構模式，將業務狀態的變更記錄為事件序列，而非直接存儲狀態。

## F

### Field

Document 中的鍵值對，相當於關聯式資料庫的 Column。

## I

### Index

索引，用於加速查詢的資料結構。

### Isolation Level

隔離級別，定義並發交易之間的可見性。

## M

### MongoDB Compass

MongoDB 官方提供的圖形化管理工具。

### MongoTemplate

Spring Data MongoDB 提供的底層操作類，支援複雜查詢。

### Multikey Index

多鍵索引，用於索引陣列欄位。

## N

### NoSQL

Not Only SQL。一類不使用傳統關聯式模型的資料庫總稱。

## O

### ObjectId

MongoDB 預設的主鍵類型，12 bytes 包含時間戳、機器 ID、程序 ID、遞增計數器。

### Oplog

Operation Log。Replica Set 中用於記錄所有寫操作的集合，支援複製功能。

## P

### Partition Tolerance

分區容錯。系統在網路分區時仍能運作的能力。

### Polyglot Persistence

混合持久化。根據不同資料特性選擇不同類型的資料庫。

### Primary Key

主鍵。MongoDB 中為 `_id` 欄位，自動建立索引。

## R

### Read Concern

讀取 Concern。定義讀取操作的隔離級別。

### Replica Set

副本集。MongoDB 的複製機制，提供高可用性。

### Repository

資料訪問層的抽象介面，Spring Data MongoDB 自動生成實作。

## S

### Schema

資料結構的定義。MongoDB 為 schemaless，但可透過 Schema Validation 進行約束。

### Sharding

分片。MongoDB 的水準擴展機制，將資料分散到多個伺服器。

### Spring Data MongoDB

 Spring 框架提供的 MongoDB 整合庫，簡化資料訪問層開發。

## T

### Testcontainers

一個 Java 庫，用於在測試中啟動 Docker 容器，如 MongoDB。

### TTL Index

Time-To-Live 索引。自動過期資料的索引。

## W

### Write Concern

寫入 Concern。定義寫入操作的確認級別。

### Write Model

在 CQRS 架構中，處理寫入操作的模型。
