# 待確認事項清單

## 文件與程式碼差異

| # | 項目 | 文件描述 | 程式碼行為 | 優先級 |
|---|------|---------|-----------|--------|
| 1 | 密碼欄位名稱 | 文件使用 `password` | admin.class.php 使用 `passwd` | 高 |
| 2 | nd_report_main 初始化 | 文件說 nd1~nd5 初始為 'X' | check_rpt_main() 插入 `nd` 和 `closs` 為 'X'，未見 nd1~nd5 | 高 |
| 3 | syslog 完整欄位 | 文件列出基本欄位 | 程式碼 syslog 含 alevel, cid, company, detail, debug 等額外欄位 | 中 |
| 4 | functions.php 非災損功能 | 未提及 | 包含考試相關邏輯 (Exam_*, make_fbank_acc, CHK_PID) | 低 (判斷為歷史遺留，不移植) |
| 5 | nd_alert3 收件人 | 文件未詳述 | 程式碼硬編碼 3 個 email 地址 | 中 |
| 6 | ajax_proc.php del_obj 回應 | 文件說回傳 "OK" | 程式碼回傳 JSON {"status":"ok","msg":"已刪除"} | 低 (以程式碼為準) |
| 7 | AJAX 請求方法 | 文件部分標示 GET | ajax_proc.php 使用 $_POST 接收參數 | 低 (以程式碼 POST 行為為準) |

## 技術面待確認

| # | 項目 | 說明 | 現行決策 |
|---|------|------|---------|
| 1 | SN 產生規則 | 原 PHP 使用 MAX(sn)+1，是否有業務邏輯依賴特定 SN | 暫用 AUTO_INCREMENT |
| 2 | nd_report_detail 與 nd_report_main 關聯 | 透過 (ndsn, cid) 而非直接 FK，確認是否正確 | 使用 Repository 查詢 |
| 3 | 災害 ID 產生規則 | YYYYMMDD+序號的精確格式 | 需確認 |
| 4 | email 發送失敗處理 | PHP 原版無重試機制 | Java 版是否需要重試 |
| 5 | 併發控制 | 多人同時通報同一災害的衝突處理 | 需確認 |

## 假設

| # | 項目 | 假設內容 | 依據 |
|---|------|---------|------|
| 1 | functions.php 非災損函式 | 不移植 (考試、銀行帳號、身分證驗證等) | 與災損系統無關 |
| 2 | company.cid = '88' | 為公會本身，不受 email 網域限制 | 程式碼中明確判斷 |
| 3 | 舊版備份檔 | _bk、_old、_fix 結尾的 PHP 檔不移植 | 為歷史版本 |
| 4 | report_old/ 資料夾 | 不移植 | 舊版報表 |
