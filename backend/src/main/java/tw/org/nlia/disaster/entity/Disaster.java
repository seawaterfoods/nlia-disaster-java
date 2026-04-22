package tw.org.nlia.disaster.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "disaster")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Disaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sn")
    private Long sn;

    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "ddate")
    private LocalDate ddate;

    @Column(name = "adate")
    private LocalDate adate;

    @Column(name = "sdate")
    private LocalDate sdate;

    @Column(name = "vdate")
    private LocalDate vdate;

    @Column(name = "claim_date")
    private LocalDate claimDate;

    @Column(name = "claim_valid")
    private LocalDate claimValid;

    @Column(name = "allow_ins", length = 100)
    private String allowIns;

    @Column(name = "email_notice", length = 1)
    private String emailNotice;

    @Column(name = "reason", length = 50)
    private String reason;

    @Column(name = "df", length = 10)
    private String df;

    @Column(name = "show_status", length = 1)
    private String showStatus;

    @Column(name = "qstatus", length = 1)
    private String qstatus;

    @Column(name = "qdate")
    private LocalDate qdate;

    @Column(name = "claim_alert")
    private Integer claimAlert;

    @Column(name = "author_cid", length = 10)
    private String authorCid;

    @Column(name = "author_sn")
    private Long authorSn;
}
