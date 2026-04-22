package tw.org.nlia.disaster.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "nd_alert")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NdAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sn")
    private Long sn;

    @Column(name = "ndsn")
    private Long ndsn;

    @Column(name = "adminsn")
    private Long adminsn;

    @Column(name = "email", length = 200)
    private String email;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "adate")
    private LocalDateTime adate;
}
