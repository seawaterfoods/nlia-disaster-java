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

@RestController
@RequestMapping("/api/customer-services")
@RequiredArgsConstructor
public class CustomerServiceController {

    private final CustomerServiceService customerServiceService;

    @GetMapping("/columns/{ndsn}")
    public ApiResponse<List<CustomerServiceColumn>> listColumns(@PathVariable Long ndsn) {
        return ApiResponse.success(customerServiceService.findColumnsByDisaster(ndsn));
    }

    @PostMapping("/columns")
    public ApiResponse<CustomerServiceColumn> createColumn(@RequestBody CustomerServiceColumn column) {
        return ApiResponse.success("建立成功", customerServiceService.createColumn(column));
    }

    @DeleteMapping("/columns/{sn}")
    public ApiResponse<Void> deleteColumn(@PathVariable Long sn) {
        customerServiceService.deleteColumn(sn);
        return ApiResponse.success("已刪除", null);
    }

    @GetMapping("/data/{ndsn}/{cid}")
    public ApiResponse<List<CustomerServiceData>> listData(@PathVariable Long ndsn, @PathVariable String cid) {
        return ApiResponse.success(customerServiceService.findDataByDisasterAndCompany(ndsn, cid));
    }

    @PostMapping("/data")
    public ApiResponse<CustomerServiceData> saveData(@RequestBody CustomerServiceData data,
                                                      Authentication authentication) {
        JwtUserDetails details = (JwtUserDetails) authentication.getDetails();
        return ApiResponse.success("儲存成功", customerServiceService.saveData(data, details.getUserSn()));
    }

    @DeleteMapping("/data/{sn}")
    public ApiResponse<Void> deleteData(@PathVariable Long sn) {
        customerServiceService.deleteData(sn);
        return ApiResponse.success("已刪除", null);
    }
}
