package tw.org.nlia.disaster.statistics.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tw.org.nlia.disaster.common.ApiResponse;
import tw.org.nlia.disaster.statistics.service.StatisticsService;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_LEVEL_3','ROLE_LEVEL_4','ROLE_LEVEL_5')")
@Tag(name = "統計報表", description = "多維度災損統計")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "依公司統計", description = "依公司維度統計災損資料")
    @GetMapping("/by-company")
    public ApiResponse<List<Map<String, Object>>> byCompany(@RequestParam Long ndsn) {
        return ApiResponse.success(statisticsService.byCompany(ndsn));
    }

    @Operation(summary = "依地區統計", description = "依地區維度統計災損資料")
    @GetMapping("/by-area")
    public ApiResponse<List<Map<String, Object>>> byArea(@RequestParam Long ndsn) {
        return ApiResponse.success(statisticsService.byArea(ndsn));
    }

    @Operation(summary = "依險種統計", description = "依險種維度統計災損資料")
    @GetMapping("/by-product")
    public ApiResponse<List<Map<String, Object>>> byProduct(@RequestParam Long ndsn) {
        return ApiResponse.success(statisticsService.byProduct(ndsn));
    }

    @Operation(summary = "依地區與險種統計", description = "依地區與險種交叉統計災損資料")
    @GetMapping("/by-area-product")
    public ApiResponse<List<Map<String, Object>>> byAreaAndProduct(@RequestParam Long ndsn) {
        return ApiResponse.success(statisticsService.byAreaAndProduct(ndsn));
    }
}
