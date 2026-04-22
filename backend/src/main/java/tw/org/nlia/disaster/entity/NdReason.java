package tw.org.nlia.disaster.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "nd_reason")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NdReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sn")
    private Long sn;

    @Column(name = "id", length = 20, unique = true)
    private String id;

    @Column(name = "content", length = 200)
    private String content;

    @Column(name = "adate")
    private LocalDateTime adate;

    @Column(name = "udate")
    private LocalDateTime udate;
}
