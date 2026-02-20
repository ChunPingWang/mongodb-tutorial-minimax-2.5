# M01: RDB vs NoSQL 思維轉換

## 學習目標

理解關聯式與文件式資料庫的本質差異，建立正確的選型思維。

## 1. Data Model 哲學比較

### 1.1 關聯式資料庫 (RDB) - 正規化設計

**核心原則**:
- **正規化 (Normalization)**: 將資料拆分到多個表中，減少資料冗餘
- **參照完整性**: 使用外鍵確保資料一致性
- **Schema-on-Write**: 寫入資料前需先定義結構

**銀行帳戶範例 (RDB)**:

```sql
-- 客戶表
CREATE TABLE customers (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100),
    created_at TIMESTAMP
);

-- 帳戶表
CREATE TABLE accounts (
    id BIGINT PRIMARY KEY,
    customer_id BIGINT REFERENCES customers(id),
    account_number VARCHAR(20),
    balance DECIMAL(19,4),
    status VARCHAR(20),
    created_at TIMESTAMP
);

-- 交易記錄表
CREATE TABLE transactions (
    id BIGINT PRIMARY KEY,
    account_id BIGINT REFERENCES accounts(id),
    amount DECIMAL(19,4),
    type VARCHAR(20),
    created_at TIMESTAMP
);
```

### 1.2 MongoDB - 文档模型

**核心原則**:
- **反正規化 (Denormalization)**: 將相關資料嵌入同一文件
- **嵌入 vs 引用**: 依查詢模式選擇資料組織方式
- **Schema-on-Read**: 寫入時不需要嚴格結構，可讀取時解釋

**銀行帳戶範例 (MongoDB)**:

```javascript
// 客戶文檔
{
    "_id": ObjectId("..."),
    "name": "張三",
    "email": "zhangsan@example.com",
    "createdAt": ISODate("2024-01-01"),
    "accounts": [
        {
            "accountNumber": "A001",
            "balance": 50000,
            "status": "ACTIVE",
            "transactions": [
                {
                    "amount": 10000,
                    "type": "DEPOSIT",
                    "createdAt": ISODate("2024-01-02")
                }
            ]
        }
    ]
}
```

### 1.3 設計哲學對比

| 面向 | RDB | MongoDB |
|------|-----|---------|
| 資料組織 | 表 (Tables) | 集合 (Collections) |
| 資料單位 | 行 (Rows) | 文檔 (Documents) |
| 關係表達 | 外鍵 (Foreign Keys) | 嵌入 (Embedding) / 引用 (References) |
| Schema | 預先定義 (Schema-on-Write) | 可選 (Schema-on-Read) |
| 擴展性 | 垂直擴展 (Scale Up) | 水平擴展 (Scale Out) |
| 查詢彈性 | 需要預先定義視圖 | Ad-hoc 查詢 |

## 2. CAP 定理與一致性模型

### 2.1 CAP 定理

CAP 定理指出分散式資料系統只能同時滿足以下三個特性中的兩個：

- **Consistency (一致性)**: 所有節點在同一時刻看到相同的資料
- **Availability (可用性)**: 每個請求都會收到非錯誤回應
- **Partition Tolerance (分區容錯)**: 系統在網路分區時仍能運作

由於網路分區不可避免，實際上我們需要在 **一致性和可用性** 之間取捨。

### 2.2 ACID vs BASE

**RDB - ACID 模型**:

| 特性 | 說明 |
|------|------|
| Atomicity | 交易是全有或全無 |
| Consistency | 交易前後資料庫狀態一致 |
| Isolation | 並發交易相互隔離 |
| Durability | 交易結果持久保存 |

**NoSQL - BASE 模型**:

| 特性 | 說明 |
|------|------|
| Basically Available | 系統保證可用性 |
| Soft State | 狀態可能隨時間變化 |
| Eventually Consistent | 系統最終會達到一致狀態 |

### 2.3 金融場景應用

**轉帳場景 - 需要強一致性**:

```
場景: 帳戶 A 轉帳 1000 元到帳戶 B

ACID 交易:
1. 檢查帳戶 A 餘額 >= 1000
2. 帳戶 A 扣款 1000
3. 帳戶 B 加款 1000
4. 記錄交易明細
5. 全部成功或全部回滾
```

**電商庫存場景 - 可接受最終一致性**:

```
場景: 商品庫存扣減

BASE 最終一致:
1. 立即扣減庫存 (可用性優先)
2. 異步更新庫存服務
3. 若失敗，重試最終一致
4. 使用者可能看到短暫的庫存不一致
```

## 3. 選型決策框架

### 3.1 何時選擇 RDB

- 需要複雜的關聯查詢和 JOIN
- 需要強一致性 (如金融交易)
- 資料結構穩定，變化較少
- 需要複雜的事務支援

### 3.2 何時選擇 MongoDB

- 資料結構多變或未知
- 需要快速的開發迭代
- 需要水準擴展能力
- 以文件/JSON 為核心的資料模型
- 需要豐富的查詢能力

### 3.3 混合架構: Polyglot Persistence

現代應用通常會根據不同資料特性選擇不同的資料庫：

```
┌─────────────────────────────────────────────────────┐
│                    應用層                            │
└─────────────────────────────────────────────────────┘
         │           │           │           │
         ▼           ▼           ▼           ▼
    ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐
    │   RDB   │ │ MongoDB │ │  Redis  │ │Cassandra│
    │ 核心帳務 │ │ 客戶360 │ │  快取   │ │  IoT   │
    └─────────┘ └─────────┘ └─────────┘ └─────────┘
```

### 3.4 銀行系統選型範例

| 業務系統 | 資料庫選擇 | 理由 |
|----------|-----------|------|
| 核心帳務 | PostgreSQL | 需要強一致性、複雜查詢 |
| 客戶關係管理 | MongoDB | 客戶資料多樣、需要靈活結構 |
| 交易快取 | Redis | 需要極低延遲 |
| 交易日誌 | Cassandra | 高寫入量 時序資料 |
