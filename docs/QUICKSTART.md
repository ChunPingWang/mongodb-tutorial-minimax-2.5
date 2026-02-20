# 快速開始指南

本指南將幫助你快速啟動 MongoDB + Spring Boot 開發環境。

## 環境需求

| 軟體 | 版本 | 說明 |
|------|------|------|
| Java | 23+ | 建議使用 SDKMAN 管理 |
| Gradle | 8.11+ | 專案建置工具 |
| Docker | Latest | 用於 Testcontainers |

## 安裝步驟

### 1. 安裝 Java 23

使用 SDKMAN 安裝：

```bash
# 安裝 SDKMAN
curl -s "https://get.sdkman.io" | bash

# 安裝 Java 23
sdk install java 23.0.2-tem

# 驗證
java -version
```

### 2. 安裝 Docker Desktop

下載並安裝 [Docker Desktop](https://www.docker.com/products/docker-desktop)。

確保 Docker 正在運行：

```bash
docker ps
```

### 3. 克隆專案

```bash
git clone https://github.com/ChunPingWang/mongodb-tutorial-minimax-2.5.git
cd mongodb-tutorial-minimax-2.5
```

## 執行第一個範例

### 選擇一個 Module

建議從 M05 開始，這是一個完整的 CRUD 範例：

```bash
cd m05-spring-data-crud
```

### 執行測試

```bash
# 使用 Gradle Wrapper
./gradlew test

# 或使用系統 Gradle
gradle test
```

### 預期輸出

```
> Task :m05-spring-data-crud:test

BUILD SUCCESSFUL in 10s
```

## 理解專案結構

每個 Module 包含以下目錄：

```
m05-spring-data-crud/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/
│   │   │       ├── domain/         # 領域模型
│   │   │       ├── repository/     # 資料訪問層
│   │   │       └── service/        # 業務邏輯層
│   │   └── resources/
│   │       └── application.yml     # 配置文件
│   └── test/
│       └── java/                   # 測試碼
├── docs/                           # 教學文件
├── build.gradle.kts                # 建置配置
└── README.md                       # 模組說明
```

## 修改範例

### 新增一個 BankAccount

```java
// 在 BankAccountService 中新增方法
public BankAccount createAccount(String accountNumber, BigDecimal initialBalance) {
    BankAccount account = new BankAccount(accountNumber, initialBalance);
    return repository.save(account);
}
```

### 新增查詢方法

```java
// 在 BankAccountRepository 中新增
List<BankAccount> findByStatus(BankAccount.AccountStatus status);
List<BankAccount> findByBalanceGreaterThan(BigDecimal amount);
```

## 調試技巧

### 查看 MongoDB 日誌

在 `application.yml` 中配置：

```yaml
logging:
  level:
    org.springframework.data.mongodb: DEBUG
    org.mongodb.driver: DEBUG
```

### 使用 MongoDB Compass

[MongoDB Compass](https://www.mongodb.com/products/compass) 是官方提供的 GUI 工具，可以：

- 視覺化查看 Collection 資料
- 執行查詢
- 管理索引

### 連接到 Testcontainers

Testcontainers 啟動的 MongoDB 會暴露隨機端口，你可以通過日誌查看實際端口：

```
2024-01-01 10:00:00.000  INFO  - MongoDB container started
2024-01-01 10:00:00.000  INFO  - MongoDB URL: mongodb://localhost:32768/testdb
```

## 常見問題

### 測試失敗

如果測試失敗，確保：
1. Docker 正在運行
2. 記憶體足夠（建議 4GB+）
3. 沒有其他程式占用 MongoDB 端口

### 建置緩慢

首次建置需要下載依賴，較為緩慢。以後可以使用 Gradle Daemon 加速：

```bash
./gradlew --daemon test
```

### IDE 支援

推薦使用 IntelliJ IDEA，可獲得最佳體驗：

1. Import Project → 選擇 build.gradle.kts
2. 等待 Gradle 同步完成
3. 設定 SDK 為 Java 23

## 後續步驟

1. 完成 M01-M04 基礎概念學習
2. 動手修改 M05-M09 CRUD 範例
3. 挑戰 M10-M14 DDD 進階主題
4. 完成 M19-M21 Capstone 專案

## 獲得幫助

- [MongoDB 官方文檔](https://docs.mongodb.com/)
- [Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/mongodb)
