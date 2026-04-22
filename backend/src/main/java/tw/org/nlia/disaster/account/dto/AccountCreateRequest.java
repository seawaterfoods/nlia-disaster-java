package tw.org.nlia.disaster.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AccountCreateRequest {

    @NotBlank(message = "請輸入帳號(Email)")
    @Email(message = "Email 格式不正確")
    private String email;

    @NotBlank(message = "請輸入密碼")
    private String password;

    @NotBlank(message = "請輸入姓名")
    private String name;

    private String title;
    private String tel;
    private String mobile;
    private String email2;

    @NotNull(message = "請選擇權限等級")
    private Integer alevel;

    private String insurance;

    @NotBlank(message = "請選擇公司")
    private String cid;
}
