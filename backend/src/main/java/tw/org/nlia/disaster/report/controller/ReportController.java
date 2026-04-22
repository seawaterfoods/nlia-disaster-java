package tw.org.nlia.disaster.report.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tw.org.nlia.disaster.common.ApiResponse;
import tw.org.nlia.disaster.config.JwtUserDetails;
import tw.org.nlia.disaster.report.dto.*;
import tw.org.nlia.disaster.report.service.ReportService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // ==================== Report Main ====================
    @GetMapping("/main/{ndsn}")
    public ApiResponse<List<ReportMainResponse>> listMain(@PathVariable Long ndsn) {
        return ApiResponse.success(reportService.findMainByDisaster(ndsn));
    }

    @GetMapping("/main/{ndsn}/{cid}")
    public ApiResponse<ReportMainResponse> getMain(@PathVariable Long ndsn, @PathVariable String cid) {
        return ApiResponse.success(reportService.findMainByDisasterAndCompany(ndsn, cid));
    }

    @PostMapping("/main/ensure")
    public ApiResponse<Void> ensureMain(@RequestBody Map<String, Object> body,
                                         Authentication authentication) {
        JwtUserDetails details = (JwtUserDetails) authentication.getDetails();
        Long ndsn = Long.valueOf(body.get("ndsn").toString());
        reportService.ensureReportMain(ndsn, details.getCid(),
                authentication.getName());
        return ApiResponse.success("通報主檔已建立", null);
    }

    @PutMapping("/main/{ndsn}/{cid}/status")
    public ApiResponse<ReportMainResponse> updateMainStatus(
            @PathVariable Long ndsn, @PathVariable String cid,
            @RequestBody Map<String, String> body) {
        return ApiResponse.success("狀態已更新",
                reportService.updateMainStatus(ndsn, cid,
                        body.get("field"), body.get("status")));
    }

    // ==================== Report Detail ====================
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

    @PostMapping("/detail")
    public ApiResponse<ReportDetailResponse> createDetail(
            @RequestBody ReportDetailRequest request,
            Authentication authentication) {
        JwtUserDetails details = (JwtUserDetails) authentication.getDetails();
        return ApiResponse.success("新增成功",
                reportService.createDetail(request, details.getUserSn()));
    }

    @PutMapping("/detail/{sn}")
    public ApiResponse<ReportDetailResponse> updateDetail(
            @PathVariable Long sn, @RequestBody ReportDetailRequest request,
            Authentication authentication) {
        JwtUserDetails details = (JwtUserDetails) authentication.getDetails();
        return ApiResponse.success("更新成功",
                reportService.updateDetail(sn, request, details.getUserSn()));
    }

    @DeleteMapping("/detail/{sn}")
    public ApiResponse<Void> deleteDetail(@PathVariable Long sn,
                                           Authentication authentication) {
        JwtUserDetails details = (JwtUserDetails) authentication.getDetails();
        reportService.deleteDetail(sn, details.getUserSn());
        return ApiResponse.success("已刪除", null);
    }
}
