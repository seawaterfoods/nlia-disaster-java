package tw.org.nlia.disaster.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "company_login")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CompanyLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sn")
    private Long sn;

    @Column(name = "cid", length = 10)
    private String cid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cid", insertable = false, updatable = false)
    private Company company;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "passwd", length = 255)
    private String password;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "title", length = 50)
    private String title;

    @Column(name = "tel", length = 30)
    private String tel;

    @Column(name = "mobile", length = 30)
    private String mobile;

    @Column(name = "email2", length = 100)
    private String email2;

    @Column(name = "alevel")
    private Integer alevel;

    @Column(name = "insurance", length = 100)
    private String insurance;

    @Column(name = "status", length = 1)
    private String status;

    @Column(name = "adate")
    private LocalDateTime adate;

    @Column(name = "udate")
    private LocalDateTime udate;

    @Column(name = "ip", length = 50)
    private String ip;

    @Column(name = "lastlogin")
    private LocalDateTime lastlogin;
}
