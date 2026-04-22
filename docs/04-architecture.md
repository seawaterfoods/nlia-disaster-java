# 架構設計

## 整體架構

```
┌─────────────────────────────────────────────────────────┐
│                    瀏覽器 (Browser)                       │
│  ┌─────────────────┐  ┌──────────────────────────────┐  │
│  │ React SPA (主)   │  │ Thymeleaf 頁面 (過渡)        │  │
│  │ Vite + TypeScript│  │                              │  │
│  └────────┬────────┘  └──────────────┬───────────────┘  │
└───────────┼──────────────────────────┼──────────────────┘
            │ REST API (JSON)          │ Server-Side Render
            ▼                          ▼
┌─────────────────────────────────────────────────────────┐
│                Spring Boot 3.4.x Backend                 │
│  ┌───────────────────────────────────────────────────┐  │
│  │ Security Filter Chain (JWT)                       │  │
│  ├───────────────────────────────────────────────────┤  │
│  │ REST Controllers          │ Thymeleaf Controllers │  │
│  ├───────────────────────────┴───────────────────────┤  │
│  │ Service Layer (Business Logic)                    │  │
│  ├───────────────────────────────────────────────────┤  │
│  │ Repository Layer (Spring Data JPA)                │  │
│  ├───────────────────────────────────────────────────┤  │
│  │ Entity Layer (JPA Entities)                       │  │
│  └───────────────────────────────────────────────────┘  │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐  │
│  │ Security │ │ Mail     │ │ Excel    │ │ Scheduler│  │
│  │ Config   │ │ Service  │ │ Service  │ │ Service  │  │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘  │
└──────────────────────┬──────────────────────────────────┘
                       │ JDBC
                       ▼
              ┌─────────────────┐
              │   MySQL 8.x     │
              │  nlia_disaster  │
              └─────────────────┘
```

## 後端分層架構

### Package 結構

```
tw.org.nlia.disaster/
├── NliaDisasterApplication.java
├── config/
│   ├── SecurityConfig.java          # Spring Security + JWT 設定
│   ├── CorsConfig.java              # CORS 設定
│   ├── JwtConfig.java               # JWT 設定
│   ├── MailConfig.java              # 郵件設定
│   └── SchedulerConfig.java         # 排程設定
├── common/
│   ├── ApiResponse.java             # 統一回應格式
│   ├── GlobalExceptionHandler.java  # 全域例外處理
│   ├── Constants.java               # 系統常數
│   ├── JwtTokenProvider.java        # JWT 工具
│   └── PasswordEncoderUtil.java     # BCrypt + MD5 相容
├── entity/                          # 15 個 JPA Entity
├── auth/
│   ├── controller/AuthController.java
│   ├── service/AuthService.java
│   └── dto/LoginRequest.java, LoginResponse.java
├── account/
│   ├── controller/AccountController.java
│   ├── service/AccountService.java
│   ├── repository/CompanyLoginRepository.java, CompanyRepository.java
│   └── dto/AccountDto.java
├── disaster/
│   ├── controller/DisasterController.java
│   ├── service/DisasterService.java
│   ├── repository/DisasterRepository.java
│   └── dto/DisasterDto.java
├── alert/
│   ├── controller/AlertController.java
│   ├── service/AlertService.java
│   └── repository/NdAlertRepository.java
├── systemconfig/
│   ├── controller/SysConfigController.java, AddrTypeController.java, NdTypeController.java, NdReasonController.java
│   ├── service/SysConfigService.java, AddrTypeService.java, NdTypeService.java, NdReasonService.java
│   └── repository/SysConfigRepository.java, AddrTypeRepository.java, NdTypeRepository.java, NdReasonRepository.java
├── report/
│   ├── controller/ReportController.java
│   ├── service/ReportService.java, ReportMainService.java
│   ├── repository/NdReportMainRepository.java, NdReportDetailRepository.java
│   └── dto/ReportDto.java
├── closs/
│   ├── controller/ClossController.java
│   ├── service/ClossService.java
│   ├── repository/NdReportClossRepository.java
│   └── dto/ClossDto.java
├── customerservice/
│   ├── controller/CustomerServiceController.java
│   ├── service/CustomerServiceService.java
│   ├── repository/CustomerServiceColumnRepository.java, CustomerServiceDataRepository.java
│   └── dto/CustomerServiceDto.java
├── statistics/
│   ├── controller/StatisticsController.java
│   ├── service/StatisticsService.java
│   └── dto/StatisticsDto.java
├── excel/
│   └── service/ExcelExportService.java, ExcelImportService.java
├── scheduler/
│   └── service/ReportMailService.java
└── syslog/
    ├── controller/SyslogController.java
    ├── service/SyslogService.java
    └── repository/SyslogRepository.java
```

## 認證方案

- 使用 JWT (JSON Web Token)
- Access Token：短效期（30 分鐘）
- Refresh Token：長效期（7 天）
- 密碼：BCrypt 加密，向下相容 MD5（登入成功後自動升級）
- 權限：基於 Level (1~5) 的角色控制

## 統一回應格式

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

錯誤回應：
```json
{
  "code": 400,
  "message": "驗證失敗",
  "errors": [
    { "field": "email", "message": "Email 格式不正確" }
  ]
}
```
