# 現況分析

## 原系統概述

- **系統名稱**：重大災害損失預估通報系統
- **擁有者**：中華民國產物保險商業同業公會
- **用途**：重大天災（颱風、地震、水災等）發生後，彙整各產險公司災損通報數據，進行統計分析、報表產出與通知管理
- **技術棧**：PHP 5.x (無框架) + MySQL + jQuery 1.9.1 + PHPExcel + FusionCharts + PHPMailer 2.1

## 架構分析

### 分層

| 層級 | 實作方式 | 問題 |
|------|---------|------|
| 展示層 | PHP + HTML 混寫、jQuery AJAX 動態載入 | 前後端高度耦合 |
| 商業邏輯 | functions.php (893 行) + admin.class.php | 邏輯集中、缺乏模組化 |
| 資料存取 | db_inc.php 封裝 mysql_* API | SQL 注入風險、已棄用 API |
| 排程 | Cron Job + PHP Script | 無錯誤處理 |

### 安全性問題

1. **密碼**：MD5 雜湊，無加鹽
2. **SQL**：字串拼接，存在 SQL Injection 風險
3. **Session**：PHP Session，無 CSRF 防護
4. **設定**：DB 帳密、SMTP 帳密硬編碼在程式碼中
5. **輸入過濾**：僅使用 addslashes()，不完整

### 技術債

1. 使用已棄用的 `mysql_*` API
2. PHPExcel 已停止維護（應用 PhpSpreadsheet 替代）
3. FusionCharts 使用 Flash（已淘汰）
4. CKEditor 版本過舊
5. functions.php 包含非本系統功能（考試相關邏輯）
6. 無測試、無 CI/CD

## 程式碼規模

| 區域 | 檔案數 | 說明 |
|------|--------|------|
| www/system/ | 21 | 系統管理頁面 |
| www/report/ | 32 | 災損通報與報表頁面 |
| www/_cron/ | 8 | 排程任務 |
| _class/ | 7 | PHP 類別庫 |
| _includes/ | 8 | 共用引入檔 |
| **合計** | ~76 | 核心 PHP 檔案 |

## 可重用資產

1. **文件**：9 份詳細的人工整理文件（架構、API、功能、DB Schema、Swagger 等）
2. **業務邏輯**：functions.php 中的核心函式（send_mail, nd_alert, nd_alert2, nd_alert3, check_rpt_main 等）
3. **資料結構**：15 張資料表的完整定義
4. **API 規格**：OpenAPI 3.0 (swagger.yaml)
