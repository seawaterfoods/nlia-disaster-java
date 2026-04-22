package tw.org.nlia.disaster.disaster.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tw.org.nlia.disaster.common.ApiResponse;
import tw.org.nlia.disaster.config.JwtUserDetails;
import tw.org.nlia.disaster.disaster.dto.DisasterRequest;
import tw.org.nlia.disaster.disaster.dto.DisasterResponse;
import tw.org.nlia.disaster.disaster.service.DisasterService;

import java.util.List;

@RestController
@RequestMapping("/api/disasters")
@RequiredArgsConstructor
public class DisasterController {

    private final DisasterService disasterService;

    @GetMapping
    public ApiResponse<List<DisasterResponse>> list(
            @RequestParam(defaultValue = "active") String filter) {
        if ("all".equals(filter)) {
            return ApiResponse.success(disasterService.findAll());
        }
        return ApiResponse.success(disasterService.findActive());
    }

    @GetMapping("/{sn}")
    public ApiResponse<DisasterResponse> getById(@PathVariable Long sn) {
        return ApiResponse.success(disasterService.findById(sn));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_LEVEL_3','ROLE_LEVEL_4')")
    public ApiResponse<DisasterResponse> create(@Valid @RequestBody DisasterRequest request,
                                                 Authentication authentication) {
        JwtUserDetails details = (JwtUserDetails) authentication.getDetails();
        return ApiResponse.success("災害事件已建立",
                disasterService.create(request, details.getCid(), details.getUserSn()));
    }

    @PutMapping("/{sn}")
    @PreAuthorize("hasAnyRole('ROLE_LEVEL_3','ROLE_LEVEL_4')")
    public ApiResponse<DisasterResponse> update(@PathVariable Long sn,
                                                 @RequestBody DisasterRequest request) {
        return ApiResponse.success("更新成功", disasterService.update(sn, request));
    }

    @DeleteMapping("/{sn}")
    @PreAuthorize("hasRole('ROLE_LEVEL_4')")
    public ApiResponse<Void> delete(@PathVariable Long sn) {
        disasterService.delete(sn);
        return ApiResponse.success("已刪除", null);
    }
}
