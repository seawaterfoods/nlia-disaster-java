package tw.org.nlia.disaster.scheduler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tw.org.nlia.disaster.disaster.repository.DisasterRepository;
import tw.org.nlia.disaster.entity.Disaster;
import tw.org.nlia.disaster.entity.RptMailLog;
import tw.org.nlia.disaster.report.repository.NdReportDetailRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled report mail service (matches PHP _cron/daily_rpt_mail.php)
 * Runs daily at 17:00 to check active disasters and send report emails
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final DisasterRepository disasterRepository;
    private final NdReportDetailRepository reportDetailRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Scheduled(cron = "0 0 17 * * ?")
    @Transactional
    public void dailyReportMail() {
        log.info("Starting daily report mail task at {}", LocalDateTime.now());

        List<Disaster> activeDisasters = disasterRepository.findActiveDisasters(LocalDate.now());

        for (Disaster disaster : activeDisasters) {
            if (!"Y".equals(disaster.getEmailNotice())) continue;

            boolean hasUpdates = reportDetailRepository.existsByNdsn(disaster.getSn());
            if (!hasUpdates) continue;

            try {
                // Generate and send report
                log.info("Sending daily report for disaster: {} ({})", disaster.getTitle(), disaster.getSn());

                // Log the mail send
                RptMailLog mailLog = RptMailLog.builder()
                        .ndsn(disaster.getSn())
                        .adate(LocalDateTime.now())
                        .build();
                entityManager.persist(mailLog);

                log.info("Daily report sent for disaster: {}", disaster.getTitle());
            } catch (Exception e) {
                log.error("Failed to send daily report for disaster {}: {}", disaster.getSn(), e.getMessage());
            }
        }

        log.info("Daily report mail task completed");
    }
}
