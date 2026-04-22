package tw.org.nlia.disaster.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "nd_report_closs")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NdReportCloss {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sn")
    private Long sn;

    @Column(name = "ndsn")
    private Long ndsn;

    @Column(name = "cid", length = 10)
    private String cid;

    @Column(name = "uname", length = 50)
    private String uname;

    @Column(name = "zip", length = 5)
    private String zip;

    @Column(name = "s1", length = 1)
    private String s1;

    @Column(name = "s2", length = 1)
    private String s2;

    @Column(name = "s3", length = 1)
    private String s3;

    @Column(name = "s4", length = 1)
    private String s4;

    @Column(name = "s5", length = 1)
    private String s5;

    @Column(name = "inum")
    private Integer inum;

    @Column(name = "dnum")
    private Integer dnum;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    @Column(name = "adminsn")
    private Long adminsn;

    @Column(name = "adate")
    private LocalDateTime adate;

    @Column(name = "udate")
    private LocalDateTime udate;

    @Column(name = "show_status", length = 1)
    private String showStatus;
}
