# M05: CRUD 操作與 Repository Pattern

## 模組簡介

本模組介紹 Spring Data MongoDB 的 CRUD 操作，涵蓋 Repository 體系、OOP/DDD 映射策略，並透過銀行帳戶實驗加深理解。

## 學習目標

- 掌握 Spring Data MongoDB 基本 CRUD
- 理解 Repository 體系
- 理解 Entity vs Value Object 映射

## 內容大綱

### 文件 (DOC)

- **M05-DOC-01**: Spring Data MongoDB Repository 體系
- **M05-DOC-02**: OOP 與 DDD 映射策略

### 實驗室 (LAB)

- **M05-LAB-01**: 銀行帳戶 CRUD
- **M05-LAB-02**: 保險保單 CRUD
- **M05-LAB-03**: 電商商品 CRUD

## 快速開始

```bash
./gradlew :m05-spring-data-crud:test
```

## 技術重點

### 核心註解

- `@Document` - 標註 Collection 對應
- `@Id` - 主鍵欄位
- `@Field` - 欄位名稱映射
- `@Indexed` - 索引建立

### Java Record

```java
// Value Object 使用 Record
public record Money(BigDecimal amount, String currency) {
    public Money add(Money other) {
        return new Money(this.amount.add(other.amount), this.currency);
    }
}
```
