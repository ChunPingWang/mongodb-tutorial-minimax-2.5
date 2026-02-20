# M01-LAB: RDB vs NoSQL 比較實驗

## 實驗目標

使用 Testcontainers 同時啟動 PostgreSQL + MongoDB，比較相同資料在兩種資料庫的儲存與查詢差異。

## 前置需求

- Java 23
- Docker Desktop (用於 Testcontainers)

## 實驗步驟

### 1. 建立比較測試

```java
package com.example.rdb;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class RdbComparisonTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void shouldConnectToPostgres() {
        assertThat(postgres.isRunning()).isTrue();
    }
}
```

### 2. 執行實驗

```bash
./gradlew :m01-rdb-vs-nosql:test
```

## 預期產出

- 了解 Testcontainers 基本用法
- 理解 RDB 正規化查詢方式
- 理解 MongoDB 文件模型查詢方式
