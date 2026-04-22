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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/disasters")
@RequiredArgsConstructor
@Tag(name = "災害事件", description = "災害事件管理 CRUD")
public class DisasterController {

    private final DisasterService disasterService;

    @Operation(summary = "查詢災害事件", description = "取得災害事件清單")
    @GetMapping
    public ApiResponse<List<DisasterResponse>> list(
            @RequestParam(defaultValue = "active") String filter) {
        if ("all".equals(filter)) {
            return ApiResponse.success(disasterService.findAll());
        }
        return ApiResponse.success(disasterService.findActive());
    }

    @Operation(summary = "取得災害事件", description = "依序號取得災害事件")
    @GetMapping("/{sn}")
    public ApiResponse<DisasterResponse> getById(@PathVariable Long sn) {
        return ApiResponse.success(disasterService.findById(sn));
    }

    @Operation(summary = "建立災害事件", description = "新增災害事件")
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_LEVEL_3','ROLE_LEVEL_4')")
    public ApiResponse<DisasterResponse> create(@Valid @RequestBody DisasterRequest request,
                                                 Authentication authentication) {
        JwtUserDetails details = (JwtUserDetails) authentication.getDetails();
        return ApiResponse.success("災害事件已建立",
                disasterService.create(request, details.getCid(), details.getUserSn()));
    }

    @Operation(summary = "更新災害事件", description = "更新災害事件資料")
    @PutMapping("/{sn}")
    @PreAuthorize("hasAnyRole('ROLE_LEVEL_3','ROLE_LEVEL_4')")
    public ApiResponse<DisasterResponse> update(@PathVariable Long sn,
                                                 @RequestBody DisasterRequest request) {
        return ApiResponse.success("更新成功", disasterService.update(sn, request));
    }

    @Operation(summary = "刪除災害事件", description = "刪除指定災害事件")
    @DeleteMapping("/{sn}")
    @PreAuthorize("hasRole('ROLE_LEVEL_4')")
    public ApiResponse<Void> delete(@PathVariable Long sn) {
        disasterService.delete(sn);
        return ApiResponse.success("已刪除", null);
    }
}
