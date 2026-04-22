package tw.org.nlia.disaster.closs.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tw.org.nlia.disaster.closs.service.ClossService;
import tw.org.nlia.disaster.common.ApiResponse;
import tw.org.nlia.disaster.config.JwtUserDetails;
import tw.org.nlia.disaster.entity.NdReportCloss;

import java.util.List;

@RestController
@RequestMapping("/api/closs")
@RequiredArgsConstructor
public class ClossController {

    private final ClossService clossService;

    @GetMapping("/{ndsn}/{cid}")
    public ApiResponse<List<NdReportCloss>> list(@PathVariable Long ndsn, @PathVariable String cid) {
        return ApiResponse.success(clossService.findByDisasterAndCompany(ndsn, cid));
    }

    @PostMapping
    public ApiResponse<NdReportCloss> create(@RequestBody NdReportCloss closs,
                                              Authentication authentication) {
        JwtUserDetails details = (JwtUserDetails) authentication.getDetails();
        return ApiResponse.success("新增成功", clossService.create(closs, details.getUserSn()));
    }

    @PutMapping("/{sn}")
    public ApiResponse<NdReportCloss> update(@PathVariable Long sn, @RequestBody NdReportCloss closs,
                                              Authentication authentication) {
        JwtUserDetails details = (JwtUserDetails) authentication.getDetails();
        return ApiResponse.success("更新成功", clossService.update(sn, closs, details.getUserSn()));
    }

    @DeleteMapping("/{sn}")
    public ApiResponse<Void> delete(@PathVariable Long sn) {
        clossService.delete(sn);
        return ApiResponse.success("已刪除", null);
    }
}
