package tw.org.nlia.disaster.account.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AccountUpdateRequest {
    private String name;
    private String title;
    private String tel;
    private String mobile;
    private String email2;
    private Integer alevel;
    private String insurance;
    private String status;
}
