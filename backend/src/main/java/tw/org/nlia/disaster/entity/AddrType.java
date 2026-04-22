package tw.org.nlia.disaster.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addr_type")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AddrType {

    @Id
    @Column(name = "asn", length = 5)
    private String asn;

    @Column(name = "csn", length = 10)
    private String csn;

    @Column(name = "cname", length = 50)
    private String cname;

    @Column(name = "aname", length = 50)
    private String aname;
}
