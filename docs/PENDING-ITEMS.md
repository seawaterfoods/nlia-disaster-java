# 待確認事項清單

> **狀態：全部已解決** — 以程式碼行為為準，文件已同步更新。

## 文件與程式碼差異 ✅ 已解決

| # | 項目 | 原差異 | 解決方式 |
|---|------|--------|---------|
| 1 | 密碼欄位名稱 | 文件使用 `password`，PHP 使用 `passwd` | ✅ Java Entity 透過 `@Column(name="passwd")` 映射，已解決 |
| 2 | nd_report_main 初始化 | 文件說 nd1~nd5 初始為 'X'，實際插入 `nd` 和 `closs` | ✅ 新增 `nd` (彙總狀態) + `lstatus` + `atime` 欄位。`nd` 透過 `deriveNdStatus()` 從 nd1~nd5 衍生，對應 PHP `check_nd()`，已解決 |
| 3 | syslog 完整欄位 | 文件列出基本欄位 | ✅ Java Entity 包含所有欄位 (alevel, cid, company, detail, debug)，已解決 |
| 4 | functions.php 非災損功能 | 包含考試相關邏輯 | ✅ 不移植，記錄為歷史遺留功能，已解決 |
| 5 | nd_alert3 收件人 | 程式碼硬編碼 3 個 email 地址 | ✅ 改為 `application.yml` 設定 (`mail.alert.recipients`) + `sys_config` fallback，已解決 |
| 6 | ajax_proc del_obj 回應 | 文件說回傳 "OK" | ✅ 使用 JSON 回應 `{"status":"ok","msg":"已刪除"}`，以程式碼行為為準，已解決 |
| 7 | AJAX 請求方法 | 文件部分標示 GET | ✅ 全部使用 POST，以程式碼行為為準，已解決 |

## 技術面待確認 ✅ 已確認

| # | 項目 | 最終決策 |
|---|------|---------|
| 1 | SN 產生規則 | ✅ 使用 AUTO_INCREMENT（非 PHP 的不安全 MAX+1 做法） |
| 2 | nd_report_detail 與 nd_report_main 關聯 | ✅ 透過 (ndsn, cid) 複合條件查詢，已文件化 |
| 3 | 災害 ID 產生規則 | ✅ YYYYMMDD + 3 碼零填充序號 (例：20240115001)，由 `DisasterService.generateDisasterId()` 產生 |
| 4 | Email 發送失敗處理 | ✅ 最多重試 3 次，間隔 2 秒。失敗記錄至 `EmailFailureLog` Entity。登入時顯示失敗通知 |
| 5 | 併發控制 | ✅ `@Version` 樂觀鎖，套用於 NdReportMain、NdReportDetail、NdReportCloss、Disaster。衝突時回傳 HTTP 409 |

## 假設 ✅ 已確認

| # | 項目 | 確認結果 |
|---|------|---------|
| 1 | functions.php 非災損函式 | ✅ 不移植（考試、銀行帳號、身分證驗證等與災損系統無關） |
| 2 | company.cid = '88' | ✅ 為公會 (NLIA) 本身，無 email 網域限制 |
| 3 | 舊版備份檔 | ✅ _bk、_old、_fix 結尾的 PHP 檔不處理 |
| 4 | report_old/ 資料夾 | ✅ 不處理（舊版報表） |
