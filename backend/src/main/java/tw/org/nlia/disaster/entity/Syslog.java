package tw.org.nlia.disaster.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "syslog")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Syslog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sn")
    private Long sn;

    @Column(name = "adminsn")
    private Long adminsn;

    @Column(name = "alevel")
    private Integer alevel;

    @Column(name = "cid", length = 10)
    private String cid;

    @Column(name = "company", length = 100)
    private String company;

    @Column(name = "loginid", length = 100)
    private String loginid;

    @Column(name = "action", length = 20)
    private String action;

    @Column(name = "mstatus", length = 1)
    private String mstatus;

    @Column(name = "fromip", length = 50)
    private String fromip;

    @Column(name = "adate")
    private Long adate;

    @Column(name = "detail", length = 500)
    private String detail;

    @Column(name = "debug", length = 500)
    private String debug;
}
