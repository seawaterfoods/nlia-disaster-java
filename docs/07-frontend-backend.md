# 前後端分離方案

## 架構概述

本專案採雙前端方案：
1. **React SPA (主前端)**：React + Vite + TypeScript，長期維護
2. **Thymeleaf (過渡前端)**：Spring Boot 內建模板引擎，用於快速驗證後端功能

## API 設計規範

### 統一回應格式

```json
// 成功
{
  "code": 200,
  "message": "success",
  "data": { ... }
}

// 分頁
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [ ... ],
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5
  }
}

// 錯誤
{
  "code": 400,
  "message": "驗證失敗",
  "errors": [
    { "field": "email", "message": "Email 格式不正確" }
  ]
}
```

### RESTful 設計原則

- 使用 HTTP Method 區分操作：GET (查詢)、POST (新增)、PUT (更新)、DELETE (刪除)
- 使用 HTTP Status Code 表示結果
- URL 使用名詞複數，不使用動詞
- 分頁使用 `?page=0&size=20`
- 排序使用 `?sort=field,direction`

## 認證方案

### JWT Flow

```
1. POST /api/auth/login
   Request:  { "email": "...", "password": "..." }
   Response: { "accessToken": "...", "refreshToken": "...", "user": {...} }

2. 後續請求帶 Authorization Header:
   Authorization: Bearer <accessToken>

3. Token 過期時用 refreshToken 換新:
   POST /api/auth/refresh
   Request:  { "refreshToken": "..." }
   Response: { "accessToken": "..." }
```

### 權限控制

- 使用 Spring Security + Custom Filter
- 在 Controller 方法上使用 @PreAuthorize 或自定義 annotation
- 例：@RequireLevel(min = 3) 表示 Level 3 以上可存取

## CORS 設定

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:5173")  // Vite dev
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowCredentials(true);
    }
}
```

## React 前端架構

```
frontend/
├── package.json
├── vite.config.ts
├── tsconfig.json
├── index.html
├── public/
├── src/
│   ├── main.tsx
│   ├── App.tsx
│   ├── api/
│   │   ├── client.ts             # Axios instance
│   │   ├── auth.ts               # 認證 API
│   │   ├── accounts.ts           # 帳號 API
│   │   ├── disasters.ts          # 災害 API
│   │   ├── reports.ts            # 通報 API
│   │   └── statistics.ts         # 報表 API
│   ├── components/
│   │   ├── Layout/               # 主框架
│   │   ├── Sidebar/              # 側邊選單
│   │   ├── Header/               # 頂部選單
│   │   └── common/               # 共用元件
│   ├── pages/
│   │   ├── Login/
│   │   ├── Dashboard/
│   │   ├── Account/
│   │   ├── Disaster/
│   │   ├── Report/
│   │   ├── Statistics/
│   │   └── SystemConfig/
│   ├── hooks/
│   │   ├── useAuth.ts
│   │   └── useApi.ts
│   ├── store/
│   │   └── authStore.ts
│   ├── types/
│   │   └── index.ts
│   └── utils/
│       └── format.ts
```

## Thymeleaf 過渡前端

Thymeleaf 頁面直接由 Spring Boot Controller 渲染，作為快速驗證後端功能的方案：

```
src/main/resources/templates/
├── login.html
├── layout.html              # 主框架 (Thymeleaf Layout)
├── fragments/
│   ├── sidebar.html
│   └── header.html
├── dashboard.html
├── account/
│   ├── list.html
│   └── detail.html
├── disaster/
│   ├── list.html
│   └── detail.html
└── report/
    ├── main.html
    └── detail.html
```
