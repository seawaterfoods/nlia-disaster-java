package tw.org.nlia.disaster.alert.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import tw.org.nlia.disaster.alert.dto.SendAlertRequest;
import tw.org.nlia.disaster.alert.service.AlertService;
import tw.org.nlia.disaster.common.ApiResponse;
import tw.org.nlia.disaster.config.JwtTokenProvider;
import tw.org.nlia.disaster.entity.EmailFailureLog;
import tw.org.nlia.disaster.entity.NdAlert;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@Tag(name = "通知告警", description = "災害通知發送與 Email 失敗追蹤")
public class AlertController {

    private final AlertService alertService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "查詢通知紀錄", description = "依災害事件查詢通知紀錄")
    @GetMapping("/{ndsn}")
    public ApiResponse<List<NdAlert>> listByDisaster(@PathVariable Long ndsn) {
        return ApiResponse.success(alertService.findByDisaster(ndsn));
    }

    @Operation(summary = "發送災害通知", description = "發送災害通知 Email")
    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('ROLE_LEVEL_3','ROLE_LEVEL_4')")
    public ApiResponse<Void> sendAlert(@Valid @RequestBody SendAlertRequest request, Authentication auth) {
        Long userSn = jwtTokenProvider.getUserSnFromAuthentication(auth);
        alertService.sendDisasterAlert(request.getNdsn(), request.getInsuranceTypes(), userSn);
        return ApiResponse.success("通知已發送", null);
    }

    @Operation(summary = "查詢未處理失敗紀錄", description = "取得所有未處理的 Email 發送失敗紀錄")
    @GetMapping("/failures")
    @PreAuthorize("hasAnyRole('ROLE_LEVEL_3','ROLE_LEVEL_4')")
    public ApiResponse<List<EmailFailureLog>> getUnresolvedFailures() {
        return ApiResponse.success(alertService.findAllUnresolvedFailures());
    }

    @Operation(summary = "標記失敗已處理", description = "將指定的 Email 發送失敗紀錄標記為已處理")
    @PostMapping("/failures/{sn}/resolve")
    @PreAuthorize("hasAnyRole('ROLE_LEVEL_3','ROLE_LEVEL_4')")
    public ApiResponse<Void> resolveFailure(@PathVariable Long sn) {
        alertService.resolveFailure(sn);
        return ApiResponse.success("已標記為已處理", null);
    }
}
