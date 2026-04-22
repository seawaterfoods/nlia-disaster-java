package tw.org.nlia.disaster.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nd_report_detail")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NdReportDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sn")
    private Long sn;

    @Column(name = "ndsn")
    private Long ndsn;

    @Column(name = "cid", length = 10)
    private String cid;

    @Column(name = "bid", length = 20)
    private String bid;

    @Column(name = "zip", length = 5)
    private String zip;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "area", length = 50)
    private String area;

    @Column(name = "hname", length = 50)
    private String hname;

    @Column(name = "bname", length = 100)
    private String bname;

    @Column(name = "pname", length = 100)
    private String pname;

    @Column(name = "nd_type_sn")
    private Long ndTypeSn;

    @Column(name = "nd_date")
    private LocalDate ndDate;

    @Column(name = "pre_cost", precision = 15, scale = 2)
    private BigDecimal preCost;

    @Column(name = "pre_inum")
    private Integer preInum;

    @Column(name = "pre_dnum")
    private Integer preDnum;

    @Column(name = "commited", precision = 15, scale = 2)
    private BigDecimal commited;

    @Column(name = "pending", precision = 15, scale = 2)
    private BigDecimal pending;

    @Column(name = "prepay", precision = 15, scale = 2)
    private BigDecimal prepay;

    @Column(name = "close", length = 1)
    private String close;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    @Column(name = "show_status", length = 1)
    private String showStatus;

    @Column(name = "adminsn")
    private Long adminsn;

    @Column(name = "add_sn")
    private Long addSn;

    @Column(name = "adate")
    private LocalDateTime adate;

    @Column(name = "udate")
    private LocalDateTime udate;

    @Column(name = "del_sn")
    private Long delSn;

    @Column(name = "del_date")
    private LocalDateTime delDate;

    @Version
    @Column(name = "version")
    private Long version;
}
