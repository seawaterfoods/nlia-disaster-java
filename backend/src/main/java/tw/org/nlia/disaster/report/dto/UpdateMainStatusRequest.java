package tw.org.nlia.disaster.report.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UpdateMainStatusRequest {
    @NotBlank(message = "欄位名稱不可為空")
    private String field;

    @NotBlank(message = "狀態值不可為空")
    private String status;
}
