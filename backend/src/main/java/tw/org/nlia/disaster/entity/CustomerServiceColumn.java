package tw.org.nlia.disaster.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customer_service_column")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CustomerServiceColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sn")
    private Long sn;

    @Column(name = "ndsn")
    private Long ndsn;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "sort")
    private Integer sort;
}
