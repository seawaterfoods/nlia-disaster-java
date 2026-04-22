# 變更紀錄

## [Unreleased]

### Phase 0 - 專案初始化
- 建立專案資料夾結構
- 建立完整文件集 (13 份)
- 需求分析與系統摘要完成
- Git 初始化

### Phase 1 - 後端骨架
- Spring Boot 3.4.1 + Gradle 8.12 初始化
- 15 個 JPA Entity 建立
- 14 個 Repository 介面
- JWT 認證架構 (Token Provider, Filter, SecurityConfig)
- 統一回應格式 (ApiResponse) 與全域例外處理
- 密碼雙重相容 (BCrypt + MD5 自動升級)
- Auth 模組 (Controller, Service, DTO)

### Phase 2 - 前端骨架
- React + Vite + TypeScript 初始化
- React Router 路由設定
- Axios API 客戶端封裝 (含 JWT 攔截器)
- Auth Context + 登入頁面
- 主框架 Layout (側邊選單 + Header)
- 各功能頁面佔位路由

### Phase 3-5 - 核心模組
- 帳號管理：CRUD、搜尋、密碼變更、Email 網域驗證
- 災害事件管理：CRUD、權限控制
- 系統組態：SysConfig / AddrType / NdType / NdReason 管理
- 系統日誌：查詢 + 權限過濾
- AuthService 單元測試 (6 個案例)

### Phase 6 - 災損通報模組
- 通報主檔自動建立 (check_rpt_main)
- 災損明細 CRUD + 軟刪除
- 自有房舍損失 CRUD
- 保戶服務措施管理
- 災害通知發送 (nd_alert)
- 金額門檻告警 (nd_alert3)

### Phase 7-9 - 統計/Excel/排程/Docker
- 四種維度統計報表 (按公司/地區/商品/地區x商品)
- Excel 匯出 (XLSX) + 匯入
- 每日 17:00 排程郵件
- Docker 多階段建置 (Backend + Frontend)
- docker-compose.yml (含 MySQL 8)
- Nginx 前端配置 + API Proxy
