package tw.org.nlia.disaster.account.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AccountResponse {
    private Long sn;
    private String cid;
    private String companyName;
    private String email;
    private String name;
    private String title;
    private String tel;
    private String mobile;
    private String email2;
    private Integer alevel;
    private String insurance;
    private String status;
    private LocalDateTime lastlogin;
    private String ip;
}
