package tw.org.nlia.disaster.syslog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tw.org.nlia.disaster.common.ApiResponse;
import tw.org.nlia.disaster.config.JwtUserDetails;
import tw.org.nlia.disaster.entity.Syslog;
import tw.org.nlia.disaster.syslog.service.SyslogService;

@RestController
@RequestMapping("/api/syslogs")
@RequiredArgsConstructor
public class SyslogController {

    private final SyslogService syslogService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_LEVEL_3','ROLE_LEVEL_4')")
    public ApiResponse<Page<Syslog>> list(
            @RequestParam(required = false) Long adminsn,
            @RequestParam(required = false) String cid,
            @RequestParam(required = false) String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {

        JwtUserDetails details = (JwtUserDetails) authentication.getDetails();
        // Level 3: can only see own company logs
        String filterCid = details.getLevel() == 3 ? details.getCid() : cid;

        return ApiResponse.success(
                syslogService.findFiltered(adminsn, filterCid, action,
                        PageRequest.of(page, size)));
    }
}
