package tw.org.nlia.disaster.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "company")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Company {

    @Id
    @Column(name = "cid", length = 10)
    private String cid;

    @Column(name = "cname", length = 100)
    private String cname;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "domain", length = 100)
    private String domain;

    @Column(name = "tel", length = 30)
    private String tel;

    @Column(name = "fax", length = 30)
    private String fax;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "status", length = 1)
    private String status;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<CompanyLogin> logins;
}
