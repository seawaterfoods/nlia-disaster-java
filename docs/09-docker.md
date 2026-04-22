# Docker 使用方式

## 快速啟動

```bash
docker-compose up -d
```

## docker-compose.yml

```yaml
version: '3.8'

services:
  db:
    image: mysql:8.0
    container_name: nlia-db
    environment:
      MYSQL_DATABASE: nlia_disaster
      MYSQL_ROOT_PASSWORD: ${DB_ROOT_PASSWORD:-rootpass}
      MYSQL_USER: nlia
      MYSQL_PASSWORD: ${DB_PASSWORD:-nliapass}
    volumes:
      - db_data:/var/lib/mysql
    ports:
      - "3306:3306"
    command: --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: nlia-backend
    depends_on:
      - db
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/nlia_disaster?useUnicode=true&characterEncoding=utf8mb4
      SPRING_DATASOURCE_USERNAME: nlia
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD:-nliapass}
      JWT_SECRET: ${JWT_SECRET:-your-secret-key}
    ports:
      - "8080:8080"

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: nlia-frontend
    depends_on:
      - backend
    ports:
      - "3000:80"

volumes:
  db_data:
```

## 環境變數

建立 `.env` 檔案：

```env
DB_ROOT_PASSWORD=rootpass
DB_PASSWORD=nliapass
JWT_SECRET=your-jwt-secret-key
```

## 常用指令

```bash
# 啟動所有服務
docker-compose up -d

# 查看日誌
docker-compose logs -f backend

# 停止所有服務
docker-compose down

# 重新建構
docker-compose up -d --build

# 清除資料
docker-compose down -v
```

## 後端 Dockerfile

```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 前端 Dockerfile

```dockerfile
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```
