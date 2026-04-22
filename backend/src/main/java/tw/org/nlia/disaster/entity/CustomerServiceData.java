package tw.org.nlia.disaster.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_service_data")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CustomerServiceData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sn")
    private Long sn;

    @Column(name = "ndsn")
    private Long ndsn;

    @Column(name = "cid", length = 10)
    private String cid;

    @Column(name = "column_sn")
    private Long columnSn;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "adminsn")
    private Long adminsn;

    @Column(name = "udate")
    private LocalDateTime udate;
}
