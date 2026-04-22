package tw.org.nlia.disaster.customerservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tw.org.nlia.disaster.common.ApiResponse;
import tw.org.nlia.disaster.config.JwtUserDetails;
import tw.org.nlia.disaster.customerservice.service.CustomerServiceService;
import tw.org.nlia.disaster.entity.CustomerServiceColumn;
import tw.org.nlia.disaster.entity.CustomerServiceData;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/customer-services")
@RequiredArgsConstructor
@Tag(name = "保戶服務措施", description = "保戶服務欄位與資料管理")
public class CustomerServiceController {

    private final CustomerServiceService customerServiceService;

    @Operation(summary = "查詢保戶服務欄位", description = "依災害事件查詢保戶服務欄位定義")
    @GetMapping("/columns/{ndsn}")
    public ApiResponse<List<CustomerServiceColumn>> listColumns(@PathVariable Long ndsn) {
        return ApiResponse.success(customerServiceService.findColumnsByDisaster(ndsn));
    }

    @Operation(summary = "建立保戶服務欄位", description = "新增保戶服務欄位定義")
    @PostMapping("/columns")
    public ApiResponse<CustomerServiceColumn> createColumn(@RequestBody CustomerServiceColumn column) {
        return ApiResponse.success("建立成功", customerServiceService.createColumn(column));
    }

    @Operation(summary = "刪除保戶服務欄位", description = "刪除指定保戶服務欄位定義")
    @DeleteMapping("/columns/{sn}")
    public ApiResponse<Void> deleteColumn(@PathVariable Long sn) {
        customerServiceService.deleteColumn(sn);
        return ApiResponse.success("已刪除", null);
    }

    @Operation(summary = "查詢保戶服務資料", description = "依災害事件與公司查詢保戶服務資料")
    @GetMapping("/data/{ndsn}/{cid}")
    public ApiResponse<List<CustomerServiceData>> listData(@PathVariable Long ndsn, @PathVariable String cid) {
        return ApiResponse.success(customerServiceService.findDataByDisasterAndCompany(ndsn, cid));
    }

    @Operation(summary = "儲存保戶服務資料", description = "新增或更新保戶服務資料")
    @PostMapping("/data")
    public ApiResponse<CustomerServiceData> saveData(@RequestBody CustomerServiceData data,
                                                      Authentication authentication) {
        JwtUserDetails details = (JwtUserDetails) authentication.getDetails();
        return ApiResponse.success("儲存成功", customerServiceService.saveData(data, details.getUserSn()));
    }

    @Operation(summary = "刪除保戶服務資料", description = "刪除指定保戶服務資料")
    @DeleteMapping("/data/{sn}")
    public ApiResponse<Void> deleteData(@PathVariable Long sn) {
        customerServiceService.deleteData(sn);
        return ApiResponse.success("已刪除", null);
    }
}
