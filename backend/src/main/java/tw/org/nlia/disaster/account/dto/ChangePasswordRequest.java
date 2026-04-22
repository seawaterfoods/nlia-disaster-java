package tw.org.nlia.disaster.account.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "請輸入原密碼")
    private String oldPassword;

    @NotBlank(message = "請輸入新密碼")
    private String newPassword;

    @NotBlank(message = "請輸入確認密碼")
    private String confirmPassword;
}
