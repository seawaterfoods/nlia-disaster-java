package tw.org.nlia.disaster.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_failure_log")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EmailFailureLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sn")
    private Long sn;

    @Column(name = "recipient", length = 200)
    private String recipient;

    @Column(name = "subject", length = 500)
    private String subject;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "max_retries")
    private Integer maxRetries;

    /** Y=resolved, N=still failed */
    @Column(name = "resolved", length = 1)
    private String resolved;

    /** Which user triggered this email (nullable for system-triggered) */
    @Column(name = "triggered_by_sn")
    private Long triggeredBySn;

    @Column(name = "adate")
    private LocalDateTime adate;

    @Column(name = "resolved_date")
    private LocalDateTime resolvedDate;
}
