package tw.org.nlia.disaster.auth.dto;

import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private UserInfo user;
    /** Unresolved email failure notifications (shown once on login) */
    private List<String> emailFailureNotifications;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private Long sn;
        private String email;
        private String name;
        private String title;
        private String cid;
        private String companyName;
        private Integer level;
        private String insurance;
    }
}
