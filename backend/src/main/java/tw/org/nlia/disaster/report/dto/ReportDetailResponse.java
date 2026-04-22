package tw.org.nlia.disaster.report.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ReportDetailResponse {
    private Long sn;
    private Long ndsn;
    private String cid;
    private String bid;
    private String zip;
    private String city;
    private String area;
    private String hname;
    private String bname;
    private String pname;
    private Long ndTypeSn;
    private LocalDate ndDate;
    private BigDecimal preCost;
    private Integer preInum;
    private Integer preDnum;
    private BigDecimal commited;
    private BigDecimal pending;
    private BigDecimal prepay;
    private String close;
    private String memo;
    private String showStatus;
    private LocalDateTime adate;
    private LocalDateTime udate;
}
