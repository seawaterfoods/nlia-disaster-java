package tw.org.nlia.disaster.statistics.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tw.org.nlia.disaster.common.ApiResponse;
import tw.org.nlia.disaster.statistics.service.StatisticsService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_LEVEL_3','ROLE_LEVEL_4','ROLE_LEVEL_5')")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/by-company")
    public ApiResponse<List<Map<String, Object>>> byCompany(@RequestParam Long ndsn) {
        return ApiResponse.success(statisticsService.byCompany(ndsn));
    }

    @GetMapping("/by-area")
    public ApiResponse<List<Map<String, Object>>> byArea(@RequestParam Long ndsn) {
        return ApiResponse.success(statisticsService.byArea(ndsn));
    }

    @GetMapping("/by-product")
    public ApiResponse<List<Map<String, Object>>> byProduct(@RequestParam Long ndsn) {
        return ApiResponse.success(statisticsService.byProduct(ndsn));
    }

    @GetMapping("/by-area-product")
    public ApiResponse<List<Map<String, Object>>> byAreaAndProduct(@RequestParam Long ndsn) {
        return ApiResponse.success(statisticsService.byAreaAndProduct(ndsn));
    }
}
