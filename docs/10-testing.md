# 測試與驗證方式

## 測試策略

### 測試層級

| 層級 | 工具 | 範圍 | 重點 |
|------|------|------|------|
| 單元測試 | JUnit 5 + Mockito | Service 層 | 業務邏輯正確性 |
| Repository 測試 | @DataJpaTest | Repository 層 | 查詢正確性 |
| 整合測試 | @SpringBootTest | Controller → DB | 端對端 |
| 前端測試 | Vitest | React 元件 | 頁面渲染、API 互動 |

### 重點測試項目

1. **認證與權限**
   - 登入成功/失敗
   - MD5 舊密碼相容
   - BCrypt 新密碼加密
   - JWT Token 驗證
   - 各 Level 權限控制

2. **金額計算**
   - 預估損失加總 (SUM pre_cost)
   - 已賠付/未決/預付加總
   - 結案邏輯：close='Y' 時 pending + prepay = 0

3. **狀態轉換**
   - 通報狀態 X → N → Y
   - 災害事件生命週期
   - 軟刪除 (show_status Y/N)

4. **告警邏輯**
   - nd_alert：新災害建立時通知
   - nd_alert3：金額超過門檻通知

5. **Excel 匯入驗證**
   - 郵遞區號比對
   - 商品代碼比對
   - 金額驗證 (> 0)
   - 日期格式驗證
   - 結案邏輯驗證
   - 有 SN → UPDATE、無 SN → INSERT

6. **統計報表**
   - 6 種報表的計算結果
   - 篩選條件正確性
   - 排除軟刪除資料 (show_status = 'Y')

## 驗證方式

### 功能驗證清單

每移植一個功能模組時，需驗證：

1. **API 回應**：Java 版 API 回傳的資料結構與語意是否與 PHP 版一致
2. **業務邏輯**：相同輸入是否產生相同輸出
3. **錯誤處理**：錯誤情境是否有相同的處理方式
4. **權限控制**：各 Level 存取範圍是否一致

### 執行測試

```bash
# 後端測試
cd backend
./gradlew test

# 前端測試
cd frontend
npm test
```
