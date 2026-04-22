# NLIA 重大災害損失預估通報系統 (Java 版)

> **Non-Life Insurance Association — Major Disaster Loss Estimation & Reporting System**

## 專案簡介

本專案為「中華民國產物保險商業同業公會」的「重大災害損失預估通報系統」Java 重構版本。
原系統以 PHP 5.x + MySQL + jQuery 建構，本版本遷移至 Java 17 + Spring Boot 3.4.x + React 前後端分離架構。

## 原始系統

- **來源專案**：`D:\project\nlia-disaster`
- **原始技術棧**：PHP 5.x + MySQL + jQuery + PHPExcel + FusionCharts
- **Java 版本**：本目錄 (`nlia-disaster-java`)

## 技術棧

| 層級 | 技術 | 版本 |
|------|------|------|
| 語言 | Java | 17 |
| 框架 | Spring Boot | 3.4.x |
| 建置工具 | Gradle | 8.12+ |
| ORM | Spring Data JPA | 3.4 |
| 資料庫 | MySQL | 8.x |
| 前端 (主) | React + Vite + TypeScript | 最新穩定版 |
| 前端 (過渡) | Thymeleaf | 隨 Spring Boot |
| 認證 | Spring Security + JWT | — |
| 郵件 | Spring Boot Mail | — |
| Excel | Apache POI | — |
| 排程 | Spring Scheduling | — |
| 容器化 | Docker + Docker Compose | — |

## 快速開始

請參閱 [啟動方式](./08-getting-started.md) 與 [Docker 使用方式](./09-docker.md)。

## 文件目錄

| # | 文件 | 說明 |
|---|------|------|
| 1 | [現況分析](./01-current-analysis.md) | PHP 專案分析 |
| 2 | [需求理解與系統摘要](./02-requirements-summary.md) | 功能清單、流程、資料結構 |
| 3 | [開發規劃](./03-development-plan.md) | 階段規劃 |
| 4 | [架構設計](./04-architecture.md) | 技術架構、分層設計 |
| 5 | [模組拆分對照表](./05-module-mapping.md) | PHP→Java 對照 |
| 6 | [資料庫映射策略](./06-database-mapping.md) | Entity 設計、關聯 |
| 7 | [前後端分離方案](./07-frontend-backend.md) | API 設計、認證方案 |
| 8 | [啟動方式](./08-getting-started.md) | 本機啟動指南 |
| 9 | [Docker 使用方式](./09-docker.md) | Docker 部署指南 |
| 10 | [測試與驗證方式](./10-testing.md) | 測試策略、驗證清單 |
| 11 | [變更紀錄](./CHANGELOG.md) | 版本變更紀錄 |
| 12 | [待確認事項清單](./PENDING-ITEMS.md) | 所有待確認事項 |
