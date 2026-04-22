package tw.org.nlia.disaster.alert.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SendAlertRequest {
    @NotNull(message = "災害事件 SN 不可為空")
    private Long ndsn;

    @NotBlank(message = "險種不可為空")
    private String insuranceTypes;
}
