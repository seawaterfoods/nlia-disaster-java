package tw.org.nlia.disaster.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "nd_report_main")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NdReportMain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sn")
    private Long sn;

    @Column(name = "ndsn")
    private Long ndsn;

    @Column(name = "cid", length = 10)
    private String cid;

    @Column(name = "nd1", length = 1)
    private String nd1;

    @Column(name = "nd2", length = 1)
    private String nd2;

    @Column(name = "nd3", length = 1)
    private String nd3;

    @Column(name = "nd4", length = 1)
    private String nd4;

    @Column(name = "nd5", length = 1)
    private String nd5;

    @Column(name = "closs", length = 1)
    private String closs;

    @Column(name = "author", length = 100)
    private String author;

    @Column(name = "adate")
    private LocalDateTime adate;

    @Column(name = "udate")
    private LocalDateTime udate;
}
