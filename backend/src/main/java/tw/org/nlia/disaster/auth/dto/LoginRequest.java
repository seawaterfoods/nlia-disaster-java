package tw.org.nlia.disaster.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "請輸入帳號")
    private String email;

    @NotBlank(message = "請輸入密碼")
    private String password;
}
