package tw.org.nlia.disaster.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nd_type")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NdType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sn")
    private Long sn;

    @Column(name = "hname", length = 50)
    private String hname;

    @Column(name = "bname", length = 100)
    private String bname;

    @Column(name = "pname", length = 100)
    private String pname;

    @Column(name = "hsort")
    private Integer hsort;
}
