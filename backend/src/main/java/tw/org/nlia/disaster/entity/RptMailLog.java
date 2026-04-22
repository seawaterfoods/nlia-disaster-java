package tw.org.nlia.disaster.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rpt_mail_log")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RptMailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sn")
    private Long sn;

    @Column(name = "ndsn")
    private Long ndsn;

    @Column(name = "adate")
    private LocalDateTime adate;
}
