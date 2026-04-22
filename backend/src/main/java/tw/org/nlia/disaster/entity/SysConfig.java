package tw.org.nlia.disaster.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sys_config")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SysConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sn")
    private Long sn;

    @Column(name = "id", length = 50, unique = true)
    private String id;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "mstatus", length = 1)
    private String mstatus;
}
