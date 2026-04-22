package tw.org.nlia.disaster.alert.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tw.org.nlia.disaster.alert.service.AlertService;
import tw.org.nlia.disaster.common.ApiResponse;
import tw.org.nlia.disaster.config.JwtTokenProvider;
import tw.org.nlia.disaster.entity.EmailFailureLog;
import tw.org.nlia.disaster.entity.NdAlert;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/{ndsn}")
    public ApiResponse<List<NdAlert>> listByDisaster(@PathVariable Long ndsn) {
        return ApiResponse.success(alertService.findByDisaster(ndsn));
    }

    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('ROLE_LEVEL_3','ROLE_LEVEL_4')")
    public ApiResponse<Void> sendAlert(@RequestBody Map<String, String> body, Authentication auth) {
        Long ndsn = Long.valueOf(body.get("ndsn"));
        String insuranceTypes = body.get("insuranceTypes");
        Long userSn = jwtTokenProvider.getUserSnFromAuthentication(auth);
        alertService.sendDisasterAlert(ndsn, insuranceTypes, userSn);
        return ApiResponse.success("通知已發送", null);
    }

    @GetMapping("/failures")
    @PreAuthorize("hasAnyRole('ROLE_LEVEL_3','ROLE_LEVEL_4')")
    public ApiResponse<List<EmailFailureLog>> getUnresolvedFailures() {
        return ApiResponse.success(alertService.findAllUnresolvedFailures());
    }

    @PostMapping("/failures/{sn}/resolve")
    @PreAuthorize("hasAnyRole('ROLE_LEVEL_3','ROLE_LEVEL_4')")
    public ApiResponse<Void> resolveFailure(@PathVariable Long sn) {
        alertService.resolveFailure(sn);
        return ApiResponse.success("已標記為已處理", null);
    }
}
