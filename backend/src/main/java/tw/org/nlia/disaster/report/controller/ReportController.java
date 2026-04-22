package tw.org.nlia.disaster.report.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import tw.org.nlia.disaster.common.ApiResponse;
import tw.org.nlia.disaster.config.JwtUserDetails;
import tw.org.nlia.disaster.report.dto.*;
import tw.org.nlia.disaster.report.service.ReportService;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "災損通報", description = "通報主檔與明細管理")
public class ReportController {

    private final ReportService reportService;

    // ==================== Report Main ====================
    @Operation(summary = "查詢通報主檔", description = "依災害事件查詢所有通報主檔")
    @GetMapping("/main/{ndsn}")
    public ApiResponse<List<ReportMainResponse>> listMain(@PathVariable Long ndsn) {
        return ApiResponse.success(reportService.findMainByDisaster(ndsn));
    }

    @Operation(summary = "取得通報主檔", description = "依災害事件與公司取得通報主檔")
    @GetMapping("/main/{ndsn}/{cid}")
    public ApiResponse<ReportMainResponse> getMain(@PathVariable Long ndsn, @PathVariable String cid) {
        return ApiResponse.success(reportService.findMainByDisasterAndCompany(ndsn, cid));
    }

    @Operation(summary = "確保通報主檔存在", description = "若通報主檔不存在則自動建立")
    @PostMapping("/main/ensure")
    public ApiResponse<Void> ensureMain(@Valid @RequestBody EnsureMainRequest request,
                                         Authentication authentication) {
        JwtUserDetails details = (JwtUserDetails) authentication.getDetails();
        reportService.ensureReportMain(request.getNdsn(), details.getCid(),
                authentication.getName());
        return ApiResponse.success("通報主檔已建立", null);
    }

    @Operation(summary = "更新通報主檔狀態", description = "更新通報主檔的欄位狀態")
    @PutMapping("/main/{ndsn}/{cid}/status")
    public ApiResponse<ReportMainResponse> updateMainStatus(
            @PathVariable Long ndsn, @PathVariable String cid,
            @Valid @RequestBody UpdateMainStatusRequest request) {
        return ApiResponse.success("狀態已更新",
                reportService.updateMainStatus(ndsn, cid,
                        request.getField(), request.getStatus()));
    }

    // ==================== Report Detail ====================
    @Operation(summary = "查詢通報明細", description = "依災害事件與公司查詢通報明細")
    @GetMapping("/detail/{ndsn}/{cid}")
    public ApiResponse<List<ReportDetailResponse>> listDetails(
            @PathVariable Long ndsn, @PathVariable String cid,
            @RequestParam(required = false) String hname) {
        if (hname != null) {
            return ApiResponse.success(
                    reportService.findDetailsByDisasterAndCompanyAndType(ndsn, cid, hname));
        }
        return ApiResponse.success(
                reportService.findDetailsByDisasterAndCompany(ndsn, cid));
    }

    @Operation(summary = "新增通報明細", description = "新增一筆通報明細")
    @PostMapping("/detail")
    public ApiResponse<ReportDetailResponse> createDetail(
            @RequestBody ReportDetailRequest request,
            Authentication authentication) {
        JwtUserDetails details = (JwtUserDetails) authentication.getDetails();
        return ApiResponse.success("新增成功",
                reportService.createDetail(request, details.getUserSn()));
    }

    @Operation(summary = "更新通報明細", description = "更新指定通報明細")
    @PutMapping("/detail/{sn}")
    public ApiResponse<ReportDetailResponse> updateDetail(
            @PathVariable Long sn, @RequestBody ReportDetailRequest request,
            Authentication authentication) {
        JwtUserDetails details = (JwtUserDetails) authentication.getDetails();
        return ApiResponse.success("更新成功",
                reportService.updateDetail(sn, request, details.getUserSn()));
    }

    @Operation(summary = "刪除通報明細", description = "刪除指定通報明細")
    @DeleteMapping("/detail/{sn}")
    public ApiResponse<Void> deleteDetail(@PathVariable Long sn,
                                           Authentication authentication) {
        JwtUserDetails details = (JwtUserDetails) authentication.getDetails();
        reportService.deleteDetail(sn, details.getUserSn());
        return ApiResponse.success("已刪除", null);
    }
}
