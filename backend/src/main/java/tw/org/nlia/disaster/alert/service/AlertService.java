package tw.org.nlia.disaster.alert.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.org.nlia.disaster.account.repository.CompanyLoginRepository;
import tw.org.nlia.disaster.account.repository.CompanyRepository;
import tw.org.nlia.disaster.alert.repository.EmailFailureLogRepository;
import tw.org.nlia.disaster.alert.repository.NdAlertRepository;
import tw.org.nlia.disaster.disaster.repository.DisasterRepository;
import tw.org.nlia.disaster.entity.*;
import tw.org.nlia.disaster.report.repository.NdReportDetailRepository;
import tw.org.nlia.disaster.systemconfig.repository.SysConfigRepository;

import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final NdAlertRepository ndAlertRepository;
    private final DisasterRepository disasterRepository;
    private final CompanyLoginRepository companyLoginRepository;
    private final CompanyRepository companyRepository;
    private final NdReportDetailRepository reportDetailRepository;
    private final SysConfigRepository sysConfigRepository;
    private final EmailFailureLogRepository emailFailureLogRepository;
    private final JavaMailSender mailSender;

    @Value("${mail.from.address:noreply@example.com}")
    private String fromAddress;

    @Value("${mail.from.name:重大災損通報系統}")
    private String fromName;

    @Value("${mail.retry.max-attempts:3}")
    private int maxRetryAttempts;

    @Value("${mail.retry.delay-ms:2000}")
    private long retryDelayMs;

    @Value("${mail.alert.recipients:}")
    private String configAlertRecipients;

    /**
     * Send disaster notification to member companies (matches PHP nd_alert)
     */
    @Transactional
    public void sendDisasterAlert(Long ndsn, String insuranceTypes, Long triggeredBySn) {
        Disaster disaster = disasterRepository.findById(ndsn).orElse(null);
        if (disaster == null) return;

        String[] insTypes = insuranceTypes.split(",");
        for (String ins : insTypes) {
            List<CompanyLogin> recipients = companyLoginRepository.findByInsuranceContaining(ins.trim());
            for (CompanyLogin recipient : recipients) {
                String email = recipient.getEmail();
                if (email == null || email.isEmpty()) {
                    email = recipient.getEmail2();
                }
                if (email != null && !email.isEmpty()) {
                    String subject = "重大災損通報：" + disaster.getTitle();
                    String body = buildAlertEmailBody(disaster);
                    sendEmailWithRetry(email, subject, body, triggeredBySn);

                    NdAlert alert = NdAlert.builder()
                            .ndsn(ndsn)
                            .adminsn(recipient.getSn())
                            .email(email)
                            .content(subject)
                            .adate(LocalDateTime.now())
                            .build();
                    ndAlertRepository.save(alert);
                }
            }
        }
    }

    /**
     * Check amount threshold alert (matches PHP nd_alert3)
     */
    @Transactional
    public void checkAmountAlert(Long ndsn, String cid, BigDecimal newAmount, Long triggeredBySn) {
        if (newAmount == null) return;

        var configOpt = sysConfigRepository.findByConfigId("claim_alert_threshold");
        BigDecimal threshold = configOpt
                .map(c -> new BigDecimal(c.getContent()))
                .orElse(new BigDecimal("10000000"));

        BigDecimal totalAmount = reportDetailRepository.sumPreCostByNdsnAndCid(ndsn, cid);
        if (totalAmount == null) totalAmount = BigDecimal.ZERO;

        if (totalAmount.compareTo(threshold) >= 0) {
            String companyName = companyRepository.findById(cid)
                    .map(Company::getCname).orElse(cid);
            Disaster disaster = disasterRepository.findById(ndsn).orElse(null);

            // Get alert recipients: sys_config → yml config → empty
            String recipientEmails = sysConfigRepository.findByConfigId("claim_alert_emails")
                    .map(SysConfig::getContent)
                    .orElse(configAlertRecipients);

            if (recipientEmails != null && !recipientEmails.isEmpty() && disaster != null) {
                String subject = String.format("【金額告警】%s - %s 通報金額已達 %s",
                        disaster.getTitle(), companyName, totalAmount.toPlainString());
                String body = String.format(
                        "災害事件：%s<br>公司：%s<br>通報累計金額：%s<br>門檻金額：%s",
                        disaster.getTitle(), companyName,
                        totalAmount.toPlainString(), threshold.toPlainString());

                for (String email : recipientEmails.split(",")) {
                    sendEmailWithRetry(email.trim(), subject, body, triggeredBySn);
                }
            }
        }
    }

    public List<NdAlert> findByDisaster(Long ndsn) {
        return ndAlertRepository.findByNdsnOrderByAdateDesc(ndsn);
    }

    public List<EmailFailureLog> findUnresolvedFailures(Long userSn) {
        return emailFailureLogRepository.findUnresolvedByUser(userSn);
    }

    public List<EmailFailureLog> findAllUnresolvedFailures() {
        return emailFailureLogRepository.findAllUnresolved();
    }

    @Transactional
    public void resolveFailure(Long sn) {
        emailFailureLogRepository.markResolved(sn);
    }

    private String buildAlertEmailBody(Disaster disaster) {
        return String.format("""
                <h3>重大災害損失預估通報</h3>
                <p>災害名稱：%s</p>
                <p>災害日期：%s</p>
                <p>%s</p>
                <p>請儘速登入系統進行通報。</p>
                """, disaster.getTitle(),
                disaster.getDdate() != null ? disaster.getDdate().toString() : "",
                disaster.getContent() != null ? disaster.getContent() : "");
    }

    /**
     * Send email with retry mechanism (max 3 attempts by default).
     * On exhaustion, creates EmailFailureLog record for login notification.
     */
    private void sendEmailWithRetry(String to, String subject, String htmlBody, Long triggeredBySn) {
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxRetryAttempts; attempt++) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setFrom(fromAddress, fromName);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(htmlBody, true);
                mailSender.send(message);
                log.info("Email sent to {} (attempt {}): {}", to, attempt, subject);
                return;
            } catch (Exception e) {
                lastException = e;
                log.warn("Email send attempt {}/{} failed for {}: {}", attempt, maxRetryAttempts, to, e.getMessage());
                if (attempt < maxRetryAttempts) {
                    try { Thread.sleep(retryDelayMs); } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        // All retries exhausted — log failure
        log.error("Email send FAILED after {} attempts to {}: {}", maxRetryAttempts, to,
                lastException != null ? lastException.getMessage() : "unknown");

        EmailFailureLog failureLog = EmailFailureLog.builder()
                .recipient(to)
                .subject(subject)
                .errorMessage(lastException != null ? lastException.getMessage() : "Unknown error")
                .retryCount(maxRetryAttempts)
                .resolved("N")
                .triggeredBySn(triggeredBySn)
                .adate(LocalDateTime.now())
                .build();
        emailFailureLogRepository.save(failureLog);
    }
}
