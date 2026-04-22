# 需求理解與系統摘要

## 一、系統主要功能列表

| # | 功能模組 | 說明 | 頁面數 |
|---|---------|------|--------|
| 1 | 認證模組 | 登入 / 登出 / 忘記密碼 | 3 |
| 2 | 公告首頁 | 災害通報狀態總覽，自動建立通報主檔 | 1 |
| 3 | 帳號管理 | 公司與使用者帳號 CRUD、險種權限、搜尋 | 6 |
| 4 | 災害事件管理 | 災害 CRUD、合併、服務措施欄位定義、通知紀錄 | 5 |
| 5 | 系統組態 | 系統設定(KV)、地區管理、商品管理、原因管理 | 6 |
| 6 | 災損通報 | 通報總覽、災損 CRUD、狀態更新、自有房舍、保戶服務 | 12 |
| 7 | 統計報表 | 依公司/地區/商品的多維度彙整（6 種報表） | 10 |
| 8 | Excel 匯出入 | 批次匯入通報資料、匯出報表 | — |
| 9 | 排程郵件 | 每日 17:00 自動寄送災損統計報表 | — |
| 10 | 系統日誌 | 操作稽核紀錄 | 1 |

## 二、核心業務流程

### 2.1 災害生命週期

```
建立災害事件 → 發送告警通知 → 通報期間 (sdate~vdate) → 理賠期間 (claim_date~claim_valid) → 結束
```

### 2.2 災損通報流程

1. 管理員建立「災害事件」，設定通報期間與適用險種
2. 系統自動發送 Email 告警通知給所有相關產險公司聯絡人
3. 各產險公司登入系統，系統自動建立該公司的通報主檔 (nd_report_main)
4. 公司填報災損資料（逐筆新增或 Excel 批次匯入）
5. 公司填報自有房舍損失
6. 公司填報保戶服務措施
7. 管理員檢視/彙整統計報表

### 2.3 金額告警流程

- 通報金額超過 `sys_config.mail_alert_amount` 門檻時
- 系統呼叫 nd_alert3() 寄送告警信給指定管理人員

### 2.4 排程郵件流程

- 每日 17:00 由 Cron Job 觸發
- 查詢進行中災害 (adate ≤ today ≤ vdate)
- 檢查自上次寄送後是否有新增/修改/刪除
- 有更新 → 產生 5 種 HTML 報表附件 → Email 寄送管理員

## 三、主要模組劃分

### 3.1 認證模組
- 登入：Email + 密碼 → MD5 比對 → 建立 Session
- 登出：銷毀 Session → 記錄 syslog
- 忘記密碼：產生隨機暫時密碼 → 更新 DB → 寄 Email

### 3.2 帳號管理
- 公司列表（管理員看全部，公司管理者看自家）
- 帳號 CRUD（含驗證：Email 網域、密碼長度、至少 1 個 Level 2）
- 險種權限設定（車/火/水/意/傷健/自有）
- 個人資料修改
- 通知設定（Level ≤ 2 強制填寫 email2 與 tel）

### 3.3 災害事件管理
- 災害 CRUD（含災害 ID 自動產生 YYYYMMDD+序號）
- 合併功能（將會員通報的災害合併至主災害）
- 服務措施欄位動態定義
- 通知紀錄查詢與重新發送

### 3.4 系統組態
- 系統設定：Key-Value 參數管理
- 地區管理：台灣縣市鄉鎮郵遞區號
- 商品管理：三層架構（險種→大分類→商品名稱）
- 原因管理：災害原因代碼

### 3.5 災損通報
- 通報總覽：各險種狀態 (X/N/Y)、金額統計
- 災損明細 CRUD（含軟刪除 show_status='N'）
- 連動選單：郵遞區號→縣市鄉鎮、險種→大分類→商品
- 結案邏輯：結案時未決+預付必須為 0

### 3.6 統計報表（6 種）
1. 依公司彙整：公司 × 險種 → 件數/金額/傷亡
2. 依地區彙整：地區 × 險種
3. 依商品彙整：公司 × 商品大分類（按險種分頁）
4. 地區×商品交叉：地區 × 商品大分類（按險種分頁）
5. 自有房舍彙整：公司 × 損害類型
6. 保戶服務彙整：公司 × 服務措施欄位

### 3.7 Excel 匯出入
- 匯出：通報內容（3 工作表）、依公司/地區/商品統計
- 匯入：20 欄 Excel 批次匯入，含完整驗證（有 SN→UPDATE、無 SN→INSERT）

## 四、關鍵資料結構

### 4.1 資料表列表（15 張）

1. company — 產險公司
2. company_login — 公司帳號
3. disaster — 災害事件
4. nd_report_main — 公司通報主檔
5. nd_report_detail — 災損通報明細
6. nd_report_closs — 自有房舍損失
7. nd_alert — 災害通知紀錄
8. nd_type — 保險商品定義
9. nd_reason — 災害原因代碼
10. customer_service_column — 服務措施欄位定義
11. customer_service_data — 服務措施內容
12. addr_type — 郵遞區號/地址
13. sys_config — 系統設定
14. syslog — 系統日誌
15. rpt_mail_log — 報表郵件紀錄

### 4.2 核心關聯

- company 1:N company_login
- company 1:N nd_report_main
- disaster 1:N nd_report_main
- disaster 1:N nd_alert
- disaster 1:N customer_service_column
- nd_report_main 1:N nd_report_detail (by ndsn+cid)
- nd_report_main 1:N nd_report_closs (by ndsn+cid)
- customer_service_column 1:N customer_service_data

### 4.3 角色權限體系

| Level | 角色 | 功能範圍 |
|-------|------|---------|
| 5 | 超級管理員 | 僅檢視彙整報表 |
| 4 | 系統管理員 | 全部系統管理功能 |
| 3 | 委員會 | 管理功能 + 報表 |
| 2 | 公司管理者 | 管理自家公司帳號 + 災損通報 |
| 1 | 一般使用者 | 僅限災損通報（限指定險種）+ 個人帳號 |

## 五、對應 API 入口

| 模組 | RESTful API 路徑 |
|------|-----------------|
| 認證 | POST /api/auth/login, POST /api/auth/logout, POST /api/auth/forgot-password |
| 帳號 | /api/accounts/**, /api/companies/** |
| 災害 | /api/disasters/** |
| 通知 | /api/alerts/** |
| 組態 | /api/config/**, /api/addresses/**, /api/products/**, /api/reasons/** |
| 通報 | /api/reports/** |
| 房舍 | /api/closs/** |
| 服務 | /api/customer-services/** |
| 報表 | /api/statistics/** |
| 匯出 | /api/export/** |
| 匯入 | /api/import/** |
| 日誌 | /api/syslogs |
| 首頁 | /api/dashboard |
