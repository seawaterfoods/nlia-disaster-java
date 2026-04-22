# 模組拆分對照表

## PHP → Java 模組對照

| PHP 檔案 | Java Controller | Java Service | 主要 API |
|---------|----------------|-------------|---------|
| login.php | AuthController | AuthService | POST /api/auth/login |
| forget.php | AuthController | AuthService | POST /api/auth/forgot-password |
| login.php?DoLogout=1 | AuthController | AuthService | POST /api/auth/logout |
| sys_publish.php | DashboardController | DashboardService | GET /api/dashboard |
| sys_account.php | CompanyController | CompanyService | GET /api/companies |
| sys_account_main.php | AccountController | AccountService | GET /api/companies/{cid}/accounts |
| sys_account_detail.php | AccountController | AccountService | GET/POST/PUT /api/accounts/{sn} |
| sys_account_detail2.php | AccountController | AccountService | PUT /api/accounts/profile |
| sys_account_detail3.php | AccountController | AccountService | PUT /api/accounts/notification |
| sys_account_search.php | AccountController | AccountService | GET /api/accounts/search |
| sys_disaster.php | DisasterController | DisasterService | GET /api/disasters |
| sys_disaster_detail.php | DisasterController | DisasterService | GET/POST/PUT /api/disasters/{sn} |
| sys_disaster_service.php | DisasterController | DisasterService | CRUD /api/disasters/{sn}/service-columns |
| sys_alert.php | AlertController | AlertService | GET /api/alerts |
| sys_alert_detail.php | AlertController | AlertService | GET /api/alerts/{ndsn} |
| sys_config.php | SysConfigController | SysConfigService | GET /api/config |
| sys_config_edit.php | SysConfigController | SysConfigService | PUT /api/config/{sn} |
| sys_cityarea.php | AddrTypeController | AddrTypeService | GET /api/addresses |
| sys_cityarea_edit.php | AddrTypeController | AddrTypeService | PUT /api/addresses/{asn} |
| sys_product.php | NdTypeController | NdTypeService | GET /api/products |
| sys_product_detail.php | NdTypeController | NdTypeService | POST/PUT /api/products/{sn} |
| sys_reason.php | NdReasonController | NdReasonService | GET /api/reasons |
| sys_reason_edit.php | NdReasonController | NdReasonService | POST/PUT /api/reasons/{sn} |
| sys_log.php | SyslogController | SyslogService | GET /api/syslogs |
| rpt_main_view.php | ReportController | ReportMainService | GET /api/reports/main/{ndm} |
| rpt_main_upd.php | ReportController | ReportMainService | PUT /api/reports/main/{sn}/status |
| rpt_main_nd.php | ReportDetailController | ReportDetailService | GET /api/reports/detail |
| rpt_edit_nd.php | ReportDetailController | ReportDetailService | POST/PUT /api/reports/detail/{sn} |
| rpt_main_nd_import.php | ImportController | ExcelImportService | POST /api/import/reports |
| rpt_detail_export.php | ExportController | ExcelExportService | GET /api/export/reports |
| rpt_main_closs.php | ClossController | ClossService | GET /api/closs |
| rpt_edit_closs.php | ClossController | ClossService | POST/PUT /api/closs/{sn} |
| rpt_main_service.php | CustomerServiceController | CustomerServiceService | GET /api/customer-services |
| rpt_edit_cm.php | CustomerServiceController | CustomerServiceService | PUT /api/customer-services |
| rpt_view1.php | StatisticsController | StatisticsService | GET /api/statistics/by-company |
| rpt_view2.php | StatisticsController | StatisticsService | GET /api/statistics/by-area |
| rpt_view3.php | StatisticsController | StatisticsService | GET /api/statistics/by-product |
| rpt_view4.php | StatisticsController | StatisticsService | GET /api/statistics/by-area-product |
| rpt_view5.php | StatisticsController | StatisticsService | GET /api/statistics/closs-summary |
| rpt_view6.php | StatisticsController | StatisticsService | GET /api/statistics/service-summary |
| rpt_view1_export.php | ExportController | ExcelExportService | GET /api/export/statistics/by-company |
| rpt_view2_export.php | ExportController | ExcelExportService | GET /api/export/statistics/by-area |
| rpt_view3_export.php | ExportController | ExcelExportService | GET /api/export/statistics/by-product |
| _cron/daily_rpt_mail.php | @Scheduled | ReportMailService | 內部排程 (每日 17:00) |
| ajax_proc.php?cmd=del_obj | 各 Controller | — | DELETE /api/{resource}/{id} |
| ajax_proc.php?cmd=chg_bname | NdTypeController | NdTypeService | GET /api/products/categories?hname= |
| ajax_proc.php?cmd=chg_pname | NdTypeController | NdTypeService | GET /api/products/names?bname= |
| ajax_proc.php?cmd=zipcode | AddrTypeController | AddrTypeService | GET /api/addresses/all |

## functions.php 函式對照

| PHP 函式 | Java 對應 | 說明 |
|---------|----------|------|
| send_mail() | MailService.sendMail() | 郵件發送 |
| nd_alert() | AlertService.sendDisasterAlert() | 災害通知 |
| nd_alert2() | AlertService.sendMemberAlert() | 會員公司災損通知 |
| nd_alert3() | AlertService.sendAmountAlert() | 金額超過門檻通知 |
| check_rpt_main() | ReportMainService.ensureReportMain() | 自動建立通報主檔 |
| display_num() | 前端 NumberFormat | 數字格式化 |
| php_mailer_send() | MailService.send() | SMTP 寄信 |
| makepass() | PasswordUtil.generateTempPassword() | 產生暫時密碼 |

## db_inc.php 函式對照

| PHP 函式 | Java 對應 | 說明 |
|---------|----------|------|
| show_rs() | Repository.findAll() / findBy*() | 多筆查詢 |
| show_rd() | Repository.findById() / findOneBy*() | 單筆查詢 |
| Show_Sum() | @Query SUM() | 加總 |
| Show_Num() | Repository.count() | 計數 |
| Show_Column() | @Query 單欄 | 單欄查詢 |
| Show_Distinct() | @Query DISTINCT | 去重查詢 |
| Insert_Sn() | JPA save() + @GeneratedValue | 新增 |
| Insert_Set() | JPA save() | 新增 |
| Update_Table() | JPA save() | 更新 |
| Delete_Table() | Repository.delete() | 刪除 |
