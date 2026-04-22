package tw.org.nlia.disaster.closs.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tw.org.nlia.disaster.closs.service.ClossService;
import tw.org.nlia.disaster.common.ApiResponse;
import tw.org.nlia.disaster.config.JwtUserDetails;
import tw.org.nlia.disaster.entity.NdReportCloss;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/closs")
@RequiredArgsConstructor
@Tag(name = "自有房舍損失", description = "公司自有房舍災損管理")
public class ClossController {

    private final ClossService clossService;

    @Operation(summary = "查詢自有房舍損失", description = "依災害事件與公司查詢自有房舍損失清單")
    @GetMapping("/{ndsn}/{cid}")
    public ApiResponse<List<NdReportCloss>> list(@PathVariable Long ndsn, @PathVariable String cid) {
        return ApiResponse.success(clossService.findByDisasterAndCompany(ndsn, cid));
    }

    @Operation(summary = "新增自有房舍損失", description = "新增一筆自有房舍損失紀錄")
    @PostMapping
    public ApiResponse<NdReportCloss> create(@RequestBody NdReportCloss closs,
                                              Authentication authentication) {
        JwtUserDetails details = (JwtUserDetails) authentication.getDetails();
        return ApiResponse.success("新增成功", clossService.create(closs, details.getUserSn()));
    }

    @Operation(summary = "更新自有房舍損失", description = "更新指定自有房舍損失紀錄")
    @PutMapping("/{sn}")
    public ApiResponse<NdReportCloss> update(@PathVariable Long sn, @RequestBody NdReportCloss closs,
                                              Authentication authentication) {
        JwtUserDetails details = (JwtUserDetails) authentication.getDetails();
        return ApiResponse.success("更新成功", clossService.update(sn, closs, details.getUserSn()));
    }

    @Operation(summary = "刪除自有房舍損失", description = "刪除指定自有房舍損失紀錄")
    @DeleteMapping("/{sn}")
    public ApiResponse<Void> delete(@PathVariable Long sn) {
        clossService.delete(sn);
        return ApiResponse.success("已刪除", null);
    }
}
