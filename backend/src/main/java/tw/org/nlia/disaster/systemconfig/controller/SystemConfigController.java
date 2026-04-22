package tw.org.nlia.disaster.systemconfig.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tw.org.nlia.disaster.common.ApiResponse;
import tw.org.nlia.disaster.entity.*;
import tw.org.nlia.disaster.systemconfig.service.SystemConfigService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    // ==================== SysConfig ====================
    @GetMapping("/config")
    @PreAuthorize("hasAnyRole('ROLE_LEVEL_3','ROLE_LEVEL_4')")
    public ApiResponse<List<SysConfig>> getAllConfigs() {
        return ApiResponse.success(systemConfigService.getAllConfigs());
    }

    @GetMapping("/config/{id}")
    public ApiResponse<SysConfig> getConfig(@PathVariable String id) {
        return ApiResponse.success(systemConfigService.getConfigByKey(id));
    }

    @PutMapping("/config/{id}")
    @PreAuthorize("hasRole('ROLE_LEVEL_4')")
    public ApiResponse<SysConfig> updateConfig(@PathVariable String id,
                                                @RequestBody Map<String, String> body) {
        return ApiResponse.success("更新成功",
                systemConfigService.updateConfig(id, body.get("content")));
    }

    // ==================== AddrType ====================
    @GetMapping("/addresses")
    public ApiResponse<List<AddrType>> getAllAddresses() {
        return ApiResponse.success(systemConfigService.getAllAddresses());
    }

    @GetMapping("/addresses/cities")
    public ApiResponse<List<String>> getCities() {
        return ApiResponse.success(systemConfigService.getDistinctCities());
    }

    @GetMapping("/addresses/areas")
    public ApiResponse<List<AddrType>> getAreas(@RequestParam String city) {
        return ApiResponse.success(systemConfigService.getAreasByCity(city));
    }

    @GetMapping("/addresses/zipcode/{asn}")
    public ApiResponse<AddrType> getByZipcode(@PathVariable String asn) {
        return ApiResponse.success(systemConfigService.getAddressByZip(asn));
    }

    // ==================== NdType (Products) ====================
    @GetMapping("/products")
    public ApiResponse<List<NdType>> getAllProducts() {
        return ApiResponse.success(systemConfigService.getAllProducts());
    }

    @GetMapping("/products/categories")
    public ApiResponse<List<String>> getCategories() {
        return ApiResponse.success(systemConfigService.getDistinctHnames());
    }

    @GetMapping("/products/names")
    public ApiResponse<List<String>> getProductNames(@RequestParam String hname) {
        return ApiResponse.success(systemConfigService.getBnamesByHname(hname));
    }

    @PostMapping("/products")
    @PreAuthorize("hasRole('ROLE_LEVEL_4')")
    public ApiResponse<NdType> createProduct(@RequestBody NdType ndType) {
        return ApiResponse.success("建立成功", systemConfigService.createProduct(ndType));
    }

    @PutMapping("/products/{sn}")
    @PreAuthorize("hasRole('ROLE_LEVEL_4')")
    public ApiResponse<NdType> updateProduct(@PathVariable Long sn, @RequestBody NdType ndType) {
        return ApiResponse.success("更新成功", systemConfigService.updateProduct(sn, ndType));
    }

    @DeleteMapping("/products/{sn}")
    @PreAuthorize("hasRole('ROLE_LEVEL_4')")
    public ApiResponse<Void> deleteProduct(@PathVariable Long sn) {
        systemConfigService.deleteProduct(sn);
        return ApiResponse.success("已刪除", null);
    }

    // ==================== NdReason ====================
    @GetMapping("/reasons")
    public ApiResponse<List<NdReason>> getAllReasons() {
        return ApiResponse.success(systemConfigService.getAllReasons());
    }

    @PostMapping("/reasons")
    @PreAuthorize("hasRole('ROLE_LEVEL_4')")
    public ApiResponse<NdReason> createReason(@RequestBody NdReason reason) {
        return ApiResponse.success("建立成功", systemConfigService.createReason(reason));
    }

    @PutMapping("/reasons/{sn}")
    @PreAuthorize("hasRole('ROLE_LEVEL_4')")
    public ApiResponse<NdReason> updateReason(@PathVariable Long sn, @RequestBody NdReason reason) {
        return ApiResponse.success("更新成功", systemConfigService.updateReason(sn, reason));
    }

    @DeleteMapping("/reasons/{sn}")
    @PreAuthorize("hasRole('ROLE_LEVEL_4')")
    public ApiResponse<Void> deleteReason(@PathVariable Long sn) {
        systemConfigService.deleteReason(sn);
        return ApiResponse.success("已刪除", null);
    }
}
