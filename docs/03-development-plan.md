# 開發規劃

## 開發階段

### Phase 0：專案初始化與文件
- 建立新專案資料夾結構
- 輸出全部規劃文件
- Git init + 初始 commit

### Phase 1：後端骨架建立
- Spring Boot 3.4.x + Gradle 初始化
- 設定 JPA、Security、CORS、Mail
- 建立全部 15 個 Entity + Repository
- 建立統一回應格式、全域例外處理
- DB schema 建立 (JPA ddl-auto + Flyway)
- Git commit

### Phase 2：前端骨架建立
- React + Vite + TypeScript 初始化
- Thymeleaf 過渡模板建立
- 路由設定
- API 呼叫封裝 (Axios)
- Login 頁面骨架
- 主框架 (含選單)
- Git commit

### Phase 3：認證與帳號管理模組
- 後端：AuthController/Service (login, logout, forgot-password)
- 後端：AccountController/Service (CRUD、搜尋、個人資料、通知設定)
- 密碼：BCrypt + MD5 相容驗證
- JWT：Access Token + Refresh Token
- 前端：Login 頁、帳號管理頁
- 單元測試
- Git commit

### Phase 4：災害事件管理模組
- 後端：DisasterController/Service (CRUD、合併、服務措施欄位)
- 後端：AlertController/Service (通知發送、紀錄查詢)
- 前端：災害管理頁、通知紀錄頁
- 單元測試
- Git commit

### Phase 5：系統組態模組
- 後端：SysConfig, AddrType, NdType, NdReason CRUD
- 前端：設定管理、地區管理、商品管理、原因管理頁
- 單元測試
- Git commit

### Phase 6：災損通報模組
- 後端：ReportController/Service (通報總覽、災損 CRUD、狀態更新)
- 後端：ClossController/Service (自有房舍 CRUD)
- 後端：CustomerServiceController/Service (服務措施 CRUD)
- 後端：金額告警邏輯 (nd_alert3 對應)
- 前端：通報相關頁面
- 單元測試
- Git commit

### Phase 7：統計報表與 Excel 匯出入
- 後端：StatisticsService (6 種報表查詢)
- 後端：ExcelService (匯出 + 匯入 + 驗證)
- 前端：報表頁面 + 匯出按鈕
- 單元測試
- Git commit

### Phase 8：排程郵件與系統日誌
- 後端：SchedulerService (每日報表郵件)
- 後端：SyslogController/Service (查詢+權限過濾)
- 前端：系統日誌頁面
- 單元測試
- Git commit

### Phase 9：Docker 化與執行環境
- Dockerfile (後端)
- Dockerfile (前端)
- docker-compose.yml (含 MySQL)
- 本機啟動指南
- 除錯方式文件
- Git commit

### Phase 10：整合測試與收尾
- 核心 API 整合測試
- 功能驗證清單核對
- 整理待確認事項
- 完善所有文件
- 最終 Git commit

## 開發原則

1. 先理解需求，再開始實作
2. 先輸出文件，再寫程式碼
3. 先做後端骨架，再做前端骨架，再逐步移植功能
4. 不一次大改，分階段完成
5. 每階段完成後保留明確結果與 Git commit
6. 重構僅限結構優化，不改變業務邏輯
7. 資訊不足時標記假設，不擅自決定
