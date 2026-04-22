# 資料庫映射策略

## 命名轉換規則

- **資料表**：JPA `@Table(name="原表名")` 保留原名
- **欄位名**：Java 使用 camelCase，JPA `@Column(name="原欄位名")` 映射
- **範例**：`nd_report_detail.pre_cost` → `NdReportDetail.preCost`

## Entity 對照表

### 1. Company — 產險公司

```java
@Entity
@Table(name = "company")
public class Company {
    @Id
    @Column(name = "cid")
    private String cid;           // 公司代碼 (PK)
    
    @Column(name = "cname")
    private String cname;          // 公司全名
    
    @Column(name = "name")
    private String name;           // 簡稱
    
    @Column(name = "domain")
    private String domain;         // Email 網域
    
    private String tel;
    private String fax;
    private String address;
    
    @Column(name = "status")
    private String status;         // Y/N
}
```

### 2. CompanyLogin — 公司帳號

```java
@Entity
@Table(name = "company_login")
public class CompanyLogin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sn;
    
    @Column(name = "cid")
    private String cid;
    
    private String email;
    
    @Column(name = "passwd")
    private String password;       // BCrypt / MD5
    
    private String name;
    private String title;
    private String tel;
    private String mobile;
    private String email2;
    
    @Column(name = "alevel")
    private Integer alevel;        // 1~5
    
    private String insurance;      // 逗號分隔
    private String status;         // Y/N
    private LocalDateTime adate;
    private LocalDateTime udate;
    private String ip;
    private LocalDateTime lastlogin;
}
```

### 3. Disaster — 災害事件

```java
@Entity
@Table(name = "disaster")
public class Disaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sn;
    
    private String id;            // 災害代碼 YYYYMMDD + 3 碼零填充序號 (例：20240115001)，由 DisasterService.generateDisasterId() 產生
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    private LocalDate ddate;      // 災害日期
    private LocalDate adate;      // 啟用日期
    private LocalDate sdate;      // 通報開始
    private LocalDate vdate;      // 通報截止
    
    @Column(name = "claim_date")
    private LocalDate claimDate;
    
    @Column(name = "claim_valid")
    private LocalDate claimValid;
    
    @Column(name = "allow_ins")
    private String allowIns;      // 逗號分隔
    
    @Column(name = "email_notice")
    private String emailNotice;   // Y/N
    
    private String reason;
    private String df;
    
    @Column(name = "show_status")
    private String showStatus;    // Y/N
    
    private String qstatus;
    private LocalDate qdate;
    
    @Column(name = "claim_alert")
    private Integer claimAlert;   // 月
    
    @Column(name = "author_cid")
    private String authorCid;
    
    @Column(name = "author_sn")
    private Long authorSn;
    
    @Version
    private Long version;     // 樂觀鎖
}
```

### 4. NdReportMain — 公司通報主檔

```java
@Entity
@Table(name = "nd_report_main")
public class NdReportMain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sn;
    
    private Long ndsn;        // FK → disaster
    private String cid;       // FK → company
    private String nd1;       // 車險 X/N/Y
    private String nd2;       // 火險
    private String nd3;       // 水險
    private String nd4;       // 意外險
    private String nd5;       // 傷健險
    private String nd;        // 彙總狀態 (由 deriveNdStatus() 從 nd1~nd5 衍生，對應 PHP check_nd())
    private String closs;     // 自有房舍
    private String lstatus;   // 列印狀態
    private String author;
    private LocalDateTime adate;
    private LocalDateTime udate;
    private Long atime;       // Unix timestamp
    
    @Version
    private Long version;     // 樂觀鎖
}
```

### 5. NdReportDetail — 災損通報明細

```java
@Entity
@Table(name = "nd_report_detail")
public class NdReportDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sn;
    
    private Long ndsn;
    private String cid;
    private String bid;           // 保單號碼
    private String zip;
    private String city;
    private String hname;         // 險種
    private String bname;         // 大分類
    private String pname;         // 商品名
    
    @Column(name = "nd_type_sn")
    private Long ndTypeSn;
    
    @Column(name = "nd_date")
    private LocalDate ndDate;     // 出險日
    
    @Column(name = "pre_cost")
    private BigDecimal preCost;   // 預估損失
    
    @Column(name = "pre_inum")
    private Integer preInum;      // 受傷人數
    
    @Column(name = "pre_dnum")
    private Integer preDnum;      // 死亡人數
    
    private BigDecimal commited;  // 已賠付
    private BigDecimal pending;   // 未決
    private BigDecimal prepay;    // 預付
    
    @Column(name = "close")
    private String close;         // Y/N
    
    @Column(columnDefinition = "TEXT")
    private String memo;
    
    @Column(name = "show_status")
    private String showStatus;    // Y=顯示, N=軟刪除
    
    private Long adminsn;
    private LocalDateTime adate;
    private LocalDateTime udate;
    
    @Column(name = "del_sn")
    private Long delSn;
    
    @Column(name = "del_date")
    private LocalDateTime delDate;
    
    @Version
    private Long version;     // 樂觀鎖
}
```

### 6~16 其他 Entity (簡述)

| Entity | 主要欄位 | 備註 |
|--------|---------|------|
| NdReportCloss | sn, ndsn, cid, uname, zip, s1~s5, inum, dnum, memo, version (@Version) | 自有房舍 |
| NdAlert | sn, ndsn, adminsn, email, adate | 通知紀錄 |
| NdType | sn, hname, bname, pname, hsort | 商品定義 |
| NdReason | sn, id, content, adate, udate | 原因代碼 |
| CustomerServiceColumn | sn, ndsn, title, sort | 服務欄位定義 |
| CustomerServiceData | sn, ndsn, cid, columnSn, content, adminsn, udate | 服務內容 |
| AddrType | asn (PK), csn, cname, aname | 郵遞區號 |
| SysConfig | sn, id, title, content, mstatus | 系統設定 |
| Syslog | sn, adminsn, loginid, action, mstatus, fromip, adate + cid, company, alevel, detail, debug | 系統日誌 |
| RptMailLog | sn, ndsn, adate | 報表郵件紀錄 |
| EmailFailureLog | sn, recipient, subject, errorMessage, retryCount, resolved, triggeredBySn, adate, resolvedDate | Email 發送失敗紀錄 |

## 主鍵策略

- 大多數表使用 `@GeneratedValue(strategy = GenerationType.IDENTITY)` (AUTO_INCREMENT)
- `company` 使用 `cid` (VARCHAR) 作為自然主鍵
- `addr_type` 使用 `asn` (郵遞區號) 作為自然主鍵

## 關聯映射

| 關聯 | JPA 配置 |
|------|---------|
| Company → CompanyLogin | @OneToMany(mappedBy="cid") |
| Company → NdReportMain | @OneToMany(mappedBy="cid") |
| Disaster → NdReportMain | @OneToMany(mappedBy="ndsn") |
| Disaster → NdAlert | @OneToMany(mappedBy="ndsn") |
| Disaster → CustomerServiceColumn | @OneToMany(mappedBy="ndsn") |

> **注意**：nd_report_detail 與 nd_report_main 的關聯是透過 (ndsn, cid) 複合條件，非標準 FK。在 Java 中建議使用 Repository 方法查詢，而非 JPA 關聯映射。

## 安全性升級

| 項目 | PHP 原做法 | Java 新做法 |
|------|-----------|-----------|
| 密碼 | MD5 (無鹽) | BCrypt (Spring Security)，向下相容 MD5 |
| SQL | 字串拼接 | JPA Parameterized Query |
| Session | PHP Session | JWT Token (stateless) |
| 設定 | 硬編碼 | application.yml + 環境變數 |
| 輸入驗證 | addslashes() | Bean Validation (@Valid) |
