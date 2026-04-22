package tw.org.nlia.disaster.disaster.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DisasterResponse {
    private Long sn;
    private String id;
    private String title;
    private String content;
    private LocalDate ddate;
    private LocalDate adate;
    private LocalDate sdate;
    private LocalDate vdate;
    private LocalDate claimDate;
    private LocalDate claimValid;
    private String allowIns;
    private String emailNotice;
    private String reason;
    private String df;
    private String showStatus;
    private String qstatus;
    private LocalDate qdate;
    private Integer claimAlert;
    private String authorCid;
    private Long authorSn;
}
