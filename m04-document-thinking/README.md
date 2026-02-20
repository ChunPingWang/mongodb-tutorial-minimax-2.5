# M04: Document 思維與基礎建模

## 模組簡介

本模組帶領開發人員從 RDB 正規化思維轉換至 Document 建模思維，掌握 Embedding vs Referencing 的決策原則。

## 學習目標

- 理解 Document 建模核心原則
- 掌握 Embedding vs Referencing 決策
- 理解 BSON 資料類型與 Java 映射

## 內容大綱

### 文件 (DOC)

- **M04-DOC-01**: Document 建模原則
- **M04-DOC-02**: BSON 資料類型與 Java 映射
- **M04-DOC-03**: 金融場景建模實戰

### 實驗室 (LAB)

- **M04-LAB-01**: Embedding vs Referencing 實驗
- **M04-LAB-02**: Java 23 特性與 MongoDB 整合

## 快速開始

```bash
./gradlew :m04-document-thinking:test
```

## 核心原則

**「一起讀取的資料，一起儲存」**

這是 Document 資料庫最重要的設計原則。
