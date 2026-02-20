# M03: 開發環境與測試基礎設施

## 模組簡介

本模組建立標準化的 MongoDB 開發與測試環境，包含 Gradle 多模組專案配置、Testcontainers 整合、BDD+TDD 雙軌測試流程。

## 學習目標

- 建立標準化的開發環境
- 掌握 Testcontainers MongoDB 測試
- 理解 BDD + TDD 測試流程

## 內容大綱

### 文件 (DOC)

- **M03-DOC-01**: Gradle 多模組專案建置指南
- **M03-DOC-02**: Testcontainers + MongoDB 測試策略
- **M03-DOC-03**: BDD + TDD 雙軌測試流程

### 實驗室 (LAB)

- **M03-LAB-01**: 專案骨架建立
- **M03-LAB-02**: BDD 基礎架構

## 快速開始

```bash
# 建置專案
./gradlew build

# 執行測試
./gradlew :m03-environment-setup:test
```

## 專案結構

```
m03-environment-setup/
├── build.gradle.kts
└── src/
    ├── main/
    │   ├── java/com/example/
    │   │   └── Application.java
    │   └── resources/
    │       └── application.yml
    └── test/
        ├── java/com/example/
        │   ├── MongoIntegrationTest.java
        │   └── steps/
        └── resources/
            └── features/
                └── mongodb.feature
```

## 技術堆疊

- Java 23
- Spring Boot 3.4.0
- Testcontainers
- Cucumber
- AssertJ
