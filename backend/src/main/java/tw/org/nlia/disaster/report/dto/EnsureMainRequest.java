package tw.org.nlia.disaster.report.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EnsureMainRequest {
    @NotNull(message = "災害事件 SN 不可為空")
    private Long ndsn;
}
