package tw.org.nlia.disaster.report.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ReportMainResponse {
    private Long sn;
    private Long ndsn;
    private String cid;
    private String companyName;
    /** 通報彙總狀態 */
    private String nd;
    private String nd1;
    private String nd2;
    private String nd3;
    private String nd4;
    private String nd5;
    private String closs;
    private String author;
    private LocalDateTime adate;
    private LocalDateTime udate;
}
