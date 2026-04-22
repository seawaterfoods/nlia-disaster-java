package tw.org.nlia.disaster.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtUserDetails {
    private Long userSn;
    private String cid;
    private Integer level;
}
