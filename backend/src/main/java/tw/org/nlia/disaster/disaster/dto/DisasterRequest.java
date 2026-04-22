package tw.org.nlia.disaster.disaster.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DisasterRequest {

    @NotBlank(message = "請輸入災害名稱")
    private String title;

    private String content;
    private LocalDate ddate;
    private LocalDate adate;
    private LocalDate sdate;
    private LocalDate vdate;
    private LocalDate claimDate;
    private LocalDate claimValid;
    private String allowIns;
    private String emailNotice;
    private String reason;
    private String df;
    private String showStatus;
    private String qstatus;
    private Integer claimAlert;
}
