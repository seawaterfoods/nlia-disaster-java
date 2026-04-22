# 啟動方式

## 前置需求

- Java 17+
- Node.js 18+
- MySQL 8.x
- Gradle 8.12+ (或使用 Gradle Wrapper)

## 資料庫設定

```sql
CREATE DATABASE nlia_disaster CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'nlia'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON nlia_disaster.* TO 'nlia'@'localhost';
```

## 後端啟動

```bash
cd backend
./gradlew bootRun
```

後端預設啟動於 http://localhost:8080

### 設定檔

編輯 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/nlia_disaster?useUnicode=true&characterEncoding=utf8mb4
    username: nlia
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update
```

### 除錯方式

使用 IDE (IntelliJ IDEA / VS Code) 以 Debug 模式啟動 Spring Boot Application。

或使用命令列：
```bash
./gradlew bootRun --debug-jvm
```
然後在 IDE 中附加遠端除錯 (Remote Debug)，連接至 port 5005。

## 前端啟動

```bash
cd frontend
npm install
npm run dev
```

前端預設啟動於 http://localhost:5173

## Thymeleaf 過渡前端

Thymeleaf 頁面隨後端一起啟動，存取 http://localhost:8080 即可。
