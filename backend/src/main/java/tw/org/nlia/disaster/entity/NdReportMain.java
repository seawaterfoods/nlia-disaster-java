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

    /** 通報彙總狀態 (derived from nd1~nd5: any Y→Y, any N→N, else X) */
    @Column(name = "nd", length = 1)
    private String nd;

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

    /** 列印狀態 */
    @Column(name = "lstatus", length = 1)
    private String lstatus;

    @Column(name = "author", length = 100)
    private String author;

    @Column(name = "adate")
    private LocalDateTime adate;

    @Column(name = "atime")
    private Long atime;

    @Column(name = "udate")
    private LocalDateTime udate;

    @Version
    @Column(name = "version")
    private Long version;

    /**
     * Derive nd summary from nd1~nd5 (matches PHP check_nd())
     * Y if any nd1~nd5 is Y, N if any is N (and none Y), else X
     */
    public String deriveNdStatus() {
        String[] fields = {nd1, nd2, nd3, nd4, nd5};
        boolean hasY = false, hasN = false;
        for (String f : fields) {
            if ("Y".equals(f)) hasY = true;
            if ("N".equals(f)) hasN = true;
        }
        if (hasY) return "Y";
        if (hasN) return "N";
        return "X";
    }
}
