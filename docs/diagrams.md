# 系統架構與流程圖 (Mermaid Diagrams)

> 本文件彙整所有 Mermaid 圖表，可在 GitHub / VS Code / 任何支援 Mermaid 的 Markdown 檢視器中直接渲染。

---

## 目錄

1. [系統架構圖 (System Architecture)](#1-系統架構圖-system-architecture)
2. [套件／模組結構圖 (Package Structure)](#2-套件模組結構圖-package-structure)
3. [ER 圖 (Entity Relationship)](#3-er-圖-entity-relationship)
4. [登入流程 (Login Sequence)](#4-登入流程-login-sequence)
5. [災損報表提交流程 (Report Submission Sequence)](#5-災損報表提交流程-report-submission-sequence)
6. [通知／警示流程 (Alert & Notification Flow)](#6-通知警示流程-alert--notification-flow)
7. [nd 狀態推導流程 (nd Status Derivation)](#7-nd-狀態推導流程-nd-status-derivation)
8. [JWT 認證流程 (JWT Authentication Flow)](#8-jwt-認證流程-jwt-authentication-flow)
9. [Docker 部署架構 (Docker Deployment)](#9-docker-部署架構-docker-deployment)
10. [報表主檔狀態機 (Report Main State Machine)](#10-報表主檔狀態機-report-main-state-machine)

---

## 1. 系統架構圖 (System Architecture)

以 C4 Model 風格呈現系統高層架構，涵蓋使用者、前端、後端、資料庫、郵件伺服器與排程器。

```mermaid
graph TB
    subgraph 使用者
        User["👤 使用者<br/>(瀏覽器)"]
    end

    subgraph Frontend["前端 (Frontend)"]
        FE["React + Vite<br/>SPA 應用程式"]
        Nginx["Nginx<br/>靜態檔案 & 反向代理"]
    end

    subgraph Backend["後端 (Backend)"]
        API["Spring Boot 3.4.1<br/>REST API Server"]
        Security["JWT Security<br/>Filter Chain"]
        Services["Service Layer<br/>業務邏輯"]
        Scheduler["Spring @Scheduled<br/>每日 17:00 報表寄送"]
    end

    subgraph DataStore["資料儲存"]
        DB[("MySQL 8.0<br/>nlia_disaster")]
    end

    subgraph External["外部服務"]
        SMTP["📧 SMTP 郵件伺服器<br/>(smtp.gmail.com:587)"]
    end

    User -->|"HTTPS"| Nginx
    Nginx -->|"靜態資源"| FE
    Nginx -->|"反向代理 /api/*"| API
    API --> Security
    Security --> Services
    Services --> DB
    Services -->|"寄送通知信"| SMTP
    Scheduler -->|"排程觸發"| Services

    style User fill:#E8F5E9,stroke:#2E7D32
    style FE fill:#E3F2FD,stroke:#1565C0
    style Nginx fill:#E3F2FD,stroke:#1565C0
    style API fill:#FFF3E0,stroke:#E65100
    style Security fill:#FFF3E0,stroke:#E65100
    style Services fill:#FFF3E0,stroke:#E65100
    style Scheduler fill:#FFF3E0,stroke:#E65100
    style DB fill:#FCE4EC,stroke:#C62828
    style SMTP fill:#F3E5F5,stroke:#6A1B9A
```

---

## 2. 套件／模組結構圖 (Package Structure)

後端 `tw.org.nlia.disaster` 套件結構，每個模組遵循 Controller → Service → Repository 分層。

```mermaid
graph LR
    Root["tw.org.nlia.disaster"]

    Root --- auth
    Root --- account
    Root --- disaster
    Root --- report
    Root --- alert
    Root --- statistics
    Root --- excel
    Root --- scheduler
    Root --- syslog
    Root --- systemconfig
    Root --- customerservice
    Root --- closs
    Root --- config
    Root --- common
    Root --- entity

    subgraph auth["auth 認證"]
        AuthC["AuthController"]
        AuthS["AuthService"]
        AuthC --> AuthS
    end

    subgraph account["account 帳號管理"]
        AccC["AccountController"]
        AccS["AccountService"]
        AccC --> AccS
    end

    subgraph disaster["disaster 災害事件"]
        DisC["DisasterController"]
        DisS["DisasterService"]
        DisC --> DisS
    end

    subgraph report["report 報表"]
        RptC["ReportController"]
        RptS["ReportService"]
        RptC --> RptS
    end

    subgraph alert["alert 通知警示"]
        AltC["AlertController"]
        AltS["AlertService"]
        AltC --> AltS
    end

    subgraph statistics["statistics 統計"]
        StatC["StatisticsController"]
        StatS["StatisticsService"]
        StatC --> StatS
    end

    subgraph excel["excel 匯出入"]
        ExcelExp["ExcelExportService"]
        ExcelImp["ExcelImportService"]
    end

    subgraph scheduler["scheduler 排程"]
        SchS["SchedulerService<br/>@Scheduled"]
    end

    subgraph syslog["syslog 系統日誌"]
        SysR["SyslogRepository"]
    end

    subgraph systemconfig["systemconfig 系統設定"]
        SysCfgR["SysConfigRepository"]
        NdTypeR["NdTypeRepository"]
        NdReasonR["NdReasonRepository"]
        AddrTypeR["AddrTypeRepository"]
    end

    subgraph customerservice["customerservice 客服"]
        CsC["CustomerServiceController"]
        CsS["CustomerServiceService"]
        CsC --> CsS
    end

    subgraph closs["closs 自有財損"]
        ClossS["ClossService"]
    end

    subgraph config["config 安全設定"]
        SecCfg["SecurityConfig"]
        JwtProv["JwtTokenProvider"]
        JwtFilt["JwtAuthenticationFilter"]
        JwtUD["JwtUserDetails"]
    end

    subgraph common["common 共用"]
        ApiResp["ApiResponse"]
        GEH["GlobalExceptionHandler"]
        Const["Constants"]
        BizEx["BusinessException"]
        PwdUtil["PasswordEncoderUtil"]
    end

    subgraph entity["entity 實體 (16)"]
        E1["Company"]
        E2["CompanyLogin"]
        E3["Disaster"]
        E4["NdReportMain"]
        E5["NdReportDetail"]
        E6["NdReportCloss"]
        E7["NdAlert"]
        E8["EmailFailureLog"]
        E9["NdType"]
        E10["NdReason"]
        E11["SysConfig"]
        E12["RptMailLog"]
        E13["Syslog"]
        E14["AddrType"]
        E15["CustomerServiceColumn"]
        E16["CustomerServiceData"]
    end
```

---

## 3. ER 圖 (Entity Relationship)

涵蓋全部 16 個 JPA 實體及其關聯。帶有 `@Version` 標記的實體支援樂觀鎖。

```mermaid
erDiagram
    Company {
        String cid PK "公司代號"
        String cname "公司名稱"
        String status "狀態 Y/N"
    }

    CompanyLogin {
        Long sn PK "流水號"
        String cid FK "公司代號"
        String email "登入 Email"
        String password "密碼 (BCrypt)"
        Integer level "權限等級 1-5"
        String showStatus "顯示狀態"
    }

    Disaster {
        Long sn PK "流水號"
        String id UK "災害編號 (YYYYMMDDNNN)"
        String dname "災害名稱"
        String showStatus "顯示狀態 Y/N"
        String emailNotice "郵件通知 Y/N"
        Long version "樂觀鎖版本"
    }

    NdReportMain {
        Long sn PK "流水號"
        Long ndsn "災害流水號"
        String cid "公司代號"
        String nd "彙總狀態 X/Y/N"
        String nd1 "財產險"
        String nd2 "海上險"
        String nd3 "傷害險"
        String nd4 "人壽險"
        String nd5 "車輛險"
        String closs "自有財損"
        Long version "樂觀鎖版本"
    }

    NdReportDetail {
        Long sn PK "流水號"
        Long ndsn "災害流水號"
        String cid "公司代號"
        BigDecimal preCost "預估金額"
        String showStatus "顯示狀態 Y/N"
        Long version "樂觀鎖版本"
    }

    NdReportCloss {
        Long sn PK "流水號"
        Long ndsn "災害流水號"
        String cid "公司代號"
        String showStatus "顯示狀態 Y/N"
        Long version "樂觀鎖版本"
    }

    NdAlert {
        Long sn PK "流水號"
        Long ndsn "災害流水號"
        String email "收件者"
        String subject "郵件主旨"
        Timestamp adate "發送時間"
    }

    EmailFailureLog {
        Long sn PK "流水號"
        String toEmail "收件者"
        String subject "郵件主旨"
        String errorMessage "錯誤訊息"
        Boolean resolved "是否已處理"
        Long triggeredBySn "觸發者 SN"
    }

    NdType {
        Long sn PK "流水號"
        String hname "大類名稱"
        String bname "中類名稱"
        String pname "小類名稱"
    }

    NdReason {
        Long sn PK "流水號"
        String reason "災損原因"
    }

    SysConfig {
        Long sn PK "流水號"
        String configId "設定代碼"
        String configValue "設定值"
    }

    RptMailLog {
        Long sn PK "流水號"
        Long ndsn "災害流水號"
        Timestamp sendDate "寄送時間"
    }

    Syslog {
        Long sn PK "流水號"
        String action "操作類型"
        String ip "來源 IP"
        Timestamp logdate "記錄時間"
    }

    AddrType {
        String asn PK "郵遞區號"
        String city "縣市"
        String area "鄉鎮區"
    }

    CustomerServiceColumn {
        Long sn PK "流水號"
        Long ndsn "災害流水號"
        String columnName "欄位名稱"
        Integer sort "排序"
    }

    CustomerServiceData {
        Long sn PK "流水號"
        Long ndsn "災害流水號"
        String cid "公司代號"
        Long columnSn "欄位流水號"
        String dataValue "資料值"
    }

    %% === Relationships ===
    Company ||--o{ CompanyLogin : "擁有帳號"
    Company ||--o{ NdReportMain : "提交報表"
    Disaster ||--o{ NdReportMain : "產生報表"
    Disaster ||--o{ NdAlert : "發送通知"
    Disaster ||--o{ CustomerServiceColumn : "定義客服欄位"
    NdReportMain ||..o{ NdReportDetail : "關聯明細 (ndsn+cid)"
    NdReportMain ||..o{ NdReportCloss : "關聯自有財損 (ndsn+cid)"
    CustomerServiceColumn ||--o{ CustomerServiceData : "填入資料"
```

> **說明**：虛線 (`..`) 表示 NdReportDetail / NdReportCloss 與 NdReportMain 之間透過 `(ndsn, cid)` 欄位關聯，但並非資料庫層級的外鍵約束。

---

## 4. 登入流程 (Login Sequence)

使用者登入系統的完整流程，包含密碼驗證、MD5 自動升級、Email 失敗通知等。

```mermaid
sequenceDiagram
    autonumber
    actor User as 👤 使用者
    participant FE as 前端 (React)
    participant API as 後端 (Spring Boot)
    participant DB as MySQL

    User->>FE: 輸入 Email + 密碼
    FE->>API: POST /api/auth/login<br/>(LoginRequest)

    API->>DB: 查詢 CompanyLogin (by email)
    DB-->>API: CompanyLogin 資料

    alt 密碼為 MD5 格式
        API->>API: 驗證 MD5 密碼
        API->>DB: 自動升級為 BCrypt
    else 密碼為 BCrypt 格式
        API->>API: BCrypt.matches() 驗證
    end

    alt 密碼錯誤
        API-->>FE: 401 認證失敗
        FE-->>User: 顯示錯誤訊息
    end

    API->>DB: 查詢 EmailFailureLog<br/>(未處理的寄信失敗)
    DB-->>API: 失敗記錄清單

    API->>DB: 寫入 Syslog (登入紀錄)

    API->>API: 產生 JWT Access Token (30 分鐘)<br/>+ Refresh Token (7 天)

    API-->>FE: LoginResponse<br/>(tokens + 使用者資訊 + email 失敗通知)

    FE->>FE: 儲存 tokens 至 localStorage

    FE-->>User: 導向 Dashboard
```

---

## 5. 災損報表提交流程 (Report Submission Sequence)

使用者提交災損報表的完整流程，包含自動建立報表主檔、金額警示等。

```mermaid
sequenceDiagram
    autonumber
    actor User as 👤 使用者
    participant FE as 前端 (React)
    participant API as 後端 (Spring Boot)
    participant DB as MySQL
    participant SMTP as 郵件伺服器

    User->>FE: 開啟災損報表頁面
    FE->>API: GET /api/disasters<br/>(取得有效災害清單)
    API->>DB: 查詢 Disaster (showStatus='Y')
    DB-->>API: 災害清單
    API-->>FE: DisasterResponse[]

    User->>FE: 選擇災害，檢視報表表單

    FE->>API: POST /api/reports/main/ensure<br/>(ndsn, cid)
    API->>DB: 查詢 NdReportMain (ndsn + cid)

    alt 報表主檔不存在
        API->>DB: 建立 NdReportMain<br/>(nd='X', nd1~nd5='X', closs='X')
    end

    DB-->>API: NdReportMain
    API-->>FE: ReportMainResponse

    User->>FE: 填寫報表明細表單
    FE->>API: POST /api/reports/details<br/>(ReportDetailRequest)

    API->>DB: 儲存 NdReportDetail
    DB-->>API: saved

    API->>DB: SUM(preCost) by (ndsn, cid)
    DB-->>API: 累計金額

    alt 金額超過門檻
        API->>DB: 查詢收件者 (sys_config / yml)
        DB-->>API: 收件者清單

        loop 最多重試 3 次
            API->>SMTP: 寄送金額警示信
            alt 寄送失敗
                API->>API: 等待 2 秒後重試
            end
        end

        alt 3 次皆失敗
            API->>DB: 儲存 EmailFailureLog
        end
    end

    API-->>FE: ReportDetailResponse
    FE-->>User: 顯示儲存成功
```

---

## 6. 通知／警示流程 (Alert & Notification Flow)

AlertService 的三大寄信情境及 EmailFailureLog 處理流程。

```mermaid
graph TD
    subgraph sendDisasterAlert["sendDisasterAlert 災害通報"]
        DA1["取得 Disaster 資訊"]
        DA2["依保險類別<br/>查詢公司收件者"]
        DA3["組合通知信內容"]
        DA4["呼叫 sendEmailWithRetry"]
        DA5["儲存 NdAlert 紀錄"]
        DA1 --> DA2 --> DA3 --> DA4 --> DA5
    end

    subgraph checkAmountAlert["checkAmountAlert 金額警示"]
        CA1["加總 preCost<br/>(ndsn + cid)"]
        CA2{"累計金額 ><br/>門檻值？"}
        CA3["從 sys_config / yml<br/>取得收件者"]
        CA4["呼叫 sendEmailWithRetry"]
        CA1 --> CA2
        CA2 -->|"是"| CA3 --> CA4
        CA2 -->|"否"| CA5["不寄信"]
    end

    subgraph sendEmailWithRetry["sendEmailWithRetry 重試機制"]
        SR1["嘗試寄送 Email"]
        SR2{"寄送成功？"}
        SR3["記錄成功"]
        SR4{"重試次數<br/>< 3？"}
        SR5["等待 2 秒<br/>再次嘗試"]
        SR6["儲存 EmailFailureLog<br/>(resolved=false)"]
        SR1 --> SR2
        SR2 -->|"成功"| SR3
        SR2 -->|"失敗"| SR4
        SR4 -->|"是"| SR5 --> SR1
        SR4 -->|"否 (已達上限)"| SR6
    end

    subgraph loginNotification["登入失敗通知"]
        LN1["使用者登入"]
        LN2["查詢 EmailFailureLog<br/>(triggeredBySn = userSn)"]
        LN3["將未處理失敗<br/>加入 LoginResponse"]
        LN1 --> LN2 --> LN3
    end

    DA4 -.->|"呼叫"| SR1
    CA4 -.->|"呼叫"| SR1

    style sendDisasterAlert fill:#E8F5E9,stroke:#2E7D32
    style checkAmountAlert fill:#FFF3E0,stroke:#E65100
    style sendEmailWithRetry fill:#FCE4EC,stroke:#C62828
    style loginNotification fill:#E3F2FD,stroke:#1565C0
```

---

## 7. nd 狀態推導流程 (nd Status Derivation)

`NdReportMain.deriveNdStatus()` 方法的判斷邏輯，根據 nd1~nd5 五個欄位值推導出彙總狀態 `nd`。

```mermaid
flowchart TD
    Start(["開始<br/>deriveNdStatus()"])
    Input["輸入: nd1, nd2, nd3, nd4, nd5<br/>每個值為 'X', 'Y', 或 'N'"]

    CheckY{"nd1~nd5 中<br/>任一為 'Y'？"}
    ReturnY["回傳 nd = 'Y'<br/>（有損失）"]

    CheckN{"nd1~nd5 中<br/>任一為 'N'？<br/>（且無 'Y'）"}
    ReturnN["回傳 nd = 'N'<br/>（無損失）"]

    ReturnX["回傳 nd = 'X'<br/>（尚未填報）"]

    Start --> Input --> CheckY
    CheckY -->|"是"| ReturnY
    CheckY -->|"否"| CheckN
    CheckN -->|"是"| ReturnN
    CheckN -->|"否"| ReturnX

    style ReturnY fill:#C8E6C9,stroke:#2E7D32,color:#1B5E20
    style ReturnN fill:#FFCDD2,stroke:#C62828,color:#B71C1C
    style ReturnX fill:#FFF9C4,stroke:#F9A825,color:#F57F17
```

---

## 8. JWT 認證流程 (JWT Authentication Flow)

每個 API 請求的 JWT 驗證與授權流程。

```mermaid
sequenceDiagram
    autonumber
    participant Client as 客戶端
    participant Filter as JwtAuthenticationFilter
    participant Provider as JwtTokenProvider
    participant SecCtx as SecurityContext
    participant Controller as Controller
    participant PreAuth as "@PreAuthorize"

    Client->>Filter: HTTP Request<br/>Authorization: Bearer {token}

    Filter->>Filter: 從 Header 擷取 Token

    alt Token 不存在或格式錯誤
        Filter->>Client: 繼續 Filter Chain<br/>(無認證，可能 403)
    end

    Filter->>Provider: validateToken(token)

    alt Token 過期或無效
        Provider-->>Filter: false
        Filter->>Client: 繼續 Filter Chain (未認證)
    end

    Provider-->>Filter: true

    Filter->>Provider: 擷取 Claims
    Provider-->>Filter: userSn, email, level, cid

    Filter->>Filter: 建立<br/>UsernamePasswordAuthenticationToken<br/>authorities = [ROLE_LEVEL_{level}]

    Filter->>SecCtx: 設定 Authentication

    Filter->>Controller: 繼續 Filter Chain

    Controller->>PreAuth: 檢查 @PreAuthorize
    PreAuth->>PreAuth: hasRole('ROLE_LEVEL_3')?

    alt 權限不足
        PreAuth-->>Client: 403 Forbidden
    else 權限通過
        Controller-->>Client: 200 OK + 回應資料
    end
```

---

## 9. Docker 部署架構 (Docker Deployment)

`docker-compose.yml` 定義的服務拓撲與網路關係。

```mermaid
graph TB
    subgraph DockerCompose["docker-compose.yml"]
        subgraph db_container["db (MySQL 8.0)"]
            MySQL["MySQL Server<br/>Port: 3306<br/>Database: nlia_disaster"]
            Volume[("db_data<br/>Volume")]
            MySQL --- Volume
        end

        subgraph backend_container["backend (Spring Boot)"]
            SpringBoot["Spring Boot App<br/>Port: 8080"]
            EnvBE["環境變數:<br/>DB_USERNAME, DB_PASSWORD<br/>JWT_SECRET<br/>MAIL_HOST, MAIL_PASSWORD"]
        end

        subgraph frontend_container["frontend (Nginx)"]
            NginxFE["Nginx Server<br/>Port: 3000"]
            React["React SPA<br/>靜態檔案"]
            ProxyConf["反向代理<br/>/api → backend:8080"]
            NginxFE --- React
            NginxFE --- ProxyConf
        end
    end

    Client["👤 使用者<br/>瀏覽器"] -->|"http://localhost:3000"| NginxFE
    NginxFE -->|"/api/*"| SpringBoot
    SpringBoot -->|"JDBC"| MySQL
    SpringBoot -->|"SMTP"| ExtMail["📧 外部郵件伺服器"]

    backend_container -.-|"depends_on:<br/>db (healthy)"| db_container
    frontend_container -.-|"depends_on:<br/>backend"| backend_container

    style db_container fill:#FCE4EC,stroke:#C62828
    style backend_container fill:#FFF3E0,stroke:#E65100
    style frontend_container fill:#E3F2FD,stroke:#1565C0
    style Client fill:#E8F5E9,stroke:#2E7D32
    style ExtMail fill:#F3E5F5,stroke:#6A1B9A
```

---

## 10. 報表主檔狀態機 (Report Main State Machine)

`NdReportMain` 的生命週期與狀態轉換。

```mermaid
stateDiagram-v2
    [*] --> 已建立: POST /api/reports/main/ensure

    state "已建立 (Created)" as 已建立 {
        [*] --> 初始值
        初始值: nd = 'X'
        初始值: nd1~nd5 = 'X'
        初始值: closs = 'X'
    }

    state "填報中 (Updating)" as 填報中 {
        state nd1_update <<choice>>
        state nd_derive <<choice>>

        更新個別欄位: PUT /reports/main/{ndsn}/{cid}/status
        更新個別欄位 --> nd1_update
        nd1_update --> nd1_Y: 設為 'Y' (有損失)
        nd1_update --> nd1_N: 設為 'N' (無損失)
        nd1_update --> nd1_X: 設為 'X' (尚未填報)

        nd1_Y --> 推導nd
        nd1_N --> 推導nd
        nd1_X --> 推導nd

        推導nd: 呼叫 deriveNdStatus()
        推導nd --> nd_derive
        nd_derive --> nd_is_Y: 任一 ndX='Y'
        nd_derive --> nd_is_N: 任一 ndX='N' 且無 'Y'
        nd_derive --> nd_is_X: 全部 ndX='X'
    }

    已建立 --> 填報中: 更新 nd1~nd5 / closs

    state "nd = 'Y' (有損失)" as nd_is_Y
    state "nd = 'N' (無損失)" as nd_is_N
    state "nd = 'X' (未填報)" as nd_is_X

    nd_is_N --> 軟刪除明細: nd 變為 'N'

    state "軟刪除明細" as 軟刪除明細 {
        刪除: 將 NdReportDetail<br/>showStatus 設為 'N'<br/>(ndsn + cid)
    }

    nd_is_Y --> 填報中: 可繼續更新
    nd_is_X --> 填報中: 可繼續更新
    軟刪除明細 --> 填報中: 可重新更新狀態
```

---

> **附註**：所有圖表使用 [Mermaid](https://mermaid.js.org/) 語法，可在 GitHub、GitLab、VS Code (Markdown Preview Mermaid Support 擴充套件)、Notion 等平台直接渲染。
