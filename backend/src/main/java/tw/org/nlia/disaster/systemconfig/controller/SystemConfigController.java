package tw.org.nlia.disaster.systemconfig.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tw.org.nlia.disaster.common.ApiResponse;
import tw.org.nlia.disaster.entity.*;
import tw.org.nlia.disaster.systemconfig.service.SystemConfigService;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "系統組態", description = "系統設定、險種、原因碼、郵遞區號管理")
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    // ==================== SysConfig ====================
    @Operation(summary = "取得所有系統設定", description = "查詢所有系統設定項目")
    @GetMapping("/config")
    @PreAuthorize("hasAnyRole('ROLE_LEVEL_3','ROLE_LEVEL_4')")
    public ApiResponse<List<SysConfig>> getAllConfigs() {
        return ApiResponse.success(systemConfigService.getAllConfigs());
    }

    @Operation(summary = "取得系統設定", description = "依鍵值取得系統設定")
    @GetMapping("/config/{id}")
    public ApiResponse<SysConfig> getConfig(@PathVariable String id) {
        return ApiResponse.success(systemConfigService.getConfigByKey(id));
    }

    @Operation(summary = "更新系統設定", description = "更新指定系統設定內容")
    @PutMapping("/config/{id}")
    @PreAuthorize("hasRole('ROLE_LEVEL_4')")
    public ApiResponse<SysConfig> updateConfig(@PathVariable String id,
                                                @RequestBody Map<String, String> body) {
        return ApiResponse.success("更新成功",
                systemConfigService.updateConfig(id, body.get("content")));
    }

    // ==================== AddrType ====================
    @Operation(summary = "取得所有郵遞區號", description = "查詢所有郵遞區號資料")
    @GetMapping("/addresses")
    public ApiResponse<List<AddrType>> getAllAddresses() {
        return ApiResponse.success(systemConfigService.getAllAddresses());
    }

    @Operation(summary = "取得城市清單", description = "查詢所有不重複的城市名稱")
    @GetMapping("/addresses/cities")
    public ApiResponse<List<String>> getCities() {
        return ApiResponse.success(systemConfigService.getDistinctCities());
    }

    @Operation(summary = "取得鄉鎮區清單", description = "依城市查詢鄉鎮區清單")
    @GetMapping("/addresses/areas")
    public ApiResponse<List<AddrType>> getAreas(@RequestParam String city) {
        return ApiResponse.success(systemConfigService.getAreasByCity(city));
    }

    @Operation(summary = "依郵遞區號查詢", description = "依郵遞區號取得地址資料")
    @GetMapping("/addresses/zipcode/{asn}")
    public ApiResponse<AddrType> getByZipcode(@PathVariable String asn) {
        return ApiResponse.success(systemConfigService.getAddressByZip(asn));
    }

    // ==================== NdType (Products) ====================
    @Operation(summary = "取得所有險種", description = "查詢所有險種資料")
    @GetMapping("/products")
    public ApiResponse<List<NdType>> getAllProducts() {
        return ApiResponse.success(systemConfigService.getAllProducts());
    }

    @Operation(summary = "取得險種大類", description = "查詢所有不重複的險種大類")
    @GetMapping("/products/categories")
    public ApiResponse<List<String>> getCategories() {
        return ApiResponse.success(systemConfigService.getDistinctHnames());
    }

    @Operation(summary = "取得險種名稱", description = "依大類查詢險種名稱清單")
    @GetMapping("/products/names")
    public ApiResponse<List<String>> getProductNames(@RequestParam String hname) {
        return ApiResponse.success(systemConfigService.getBnamesByHname(hname));
    }

    @Operation(summary = "建立險種", description = "新增險種資料")
    @PostMapping("/products")
    @PreAuthorize("hasRole('ROLE_LEVEL_4')")
    public ApiResponse<NdType> createProduct(@RequestBody NdType ndType) {
        return ApiResponse.success("建立成功", systemConfigService.createProduct(ndType));
    }

    @Operation(summary = "更新險種", description = "更新指定險種資料")
    @PutMapping("/products/{sn}")
    @PreAuthorize("hasRole('ROLE_LEVEL_4')")
    public ApiResponse<NdType> updateProduct(@PathVariable Long sn, @RequestBody NdType ndType) {
        return ApiResponse.success("更新成功", systemConfigService.updateProduct(sn, ndType));
    }

    @Operation(summary = "刪除險種", description = "刪除指定險種資料")
    @DeleteMapping("/products/{sn}")
    @PreAuthorize("hasRole('ROLE_LEVEL_4')")
    public ApiResponse<Void> deleteProduct(@PathVariable Long sn) {
        systemConfigService.deleteProduct(sn);
        return ApiResponse.success("已刪除", null);
    }

    // ==================== NdReason ====================
    @Operation(summary = "取得所有原因碼", description = "查詢所有原因碼資料")
    @GetMapping("/reasons")
    public ApiResponse<List<NdReason>> getAllReasons() {
        return ApiResponse.success(systemConfigService.getAllReasons());
    }

    @Operation(summary = "建立原因碼", description = "新增原因碼資料")
    @PostMapping("/reasons")
    @PreAuthorize("hasRole('ROLE_LEVEL_4')")
    public ApiResponse<NdReason> createReason(@RequestBody NdReason reason) {
        return ApiResponse.success("建立成功", systemConfigService.createReason(reason));
    }

    @Operation(summary = "更新原因碼", description = "更新指定原因碼資料")
    @PutMapping("/reasons/{sn}")
    @PreAuthorize("hasRole('ROLE_LEVEL_4')")
    public ApiResponse<NdReason> updateReason(@PathVariable Long sn, @RequestBody NdReason reason) {
        return ApiResponse.success("更新成功", systemConfigService.updateReason(sn, reason));
    }

    @Operation(summary = "刪除原因碼", description = "刪除指定原因碼資料")
    @DeleteMapping("/reasons/{sn}")
    @PreAuthorize("hasRole('ROLE_LEVEL_4')")
    public ApiResponse<Void> deleteReason(@PathVariable Long sn) {
        systemConfigService.deleteReason(sn);
        return ApiResponse.success("已刪除", null);
    }
}
