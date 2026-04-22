package tw.org.nlia.disaster.account.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tw.org.nlia.disaster.account.dto.*;
import tw.org.nlia.disaster.account.service.AccountService;
import tw.org.nlia.disaster.common.ApiResponse;
import tw.org.nlia.disaster.common.Constants;
import tw.org.nlia.disaster.config.JwtUserDetails;
import tw.org.nlia.disaster.entity.Company;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ApiResponse<Page<AccountResponse>> search(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String cid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {

        JwtUserDetails details = (JwtUserDetails) authentication.getDetails();
        // Level 1-2: only see own company; Level 3+: can see all
        String filterCid = details.getLevel() <= 2 ? details.getCid() : cid;

        return ApiResponse.success(
                accountService.search(email, name, filterCid, PageRequest.of(page, size)));
    }

    @GetMapping("/{sn}")
    public ApiResponse<AccountResponse> getById(@PathVariable Long sn) {
        return ApiResponse.success(accountService.findById(sn));
    }

    @GetMapping("/company/{cid}")
    public ApiResponse<List<AccountResponse>> getByCompany(@PathVariable String cid) {
        return ApiResponse.success(accountService.findByCid(cid));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_LEVEL_2','ROLE_LEVEL_3','ROLE_LEVEL_4')")
    public ApiResponse<AccountResponse> create(@Valid @RequestBody AccountCreateRequest request,
                                                Authentication authentication) {
        JwtUserDetails details = (JwtUserDetails) authentication.getDetails();
        // Level 2 can only create accounts for own company
        if (details.getLevel() == 2) {
            request.setCid(details.getCid());
        }
        return ApiResponse.success("帳號建立成功", accountService.create(request));
    }

    @PutMapping("/{sn}")
    public ApiResponse<AccountResponse> update(@PathVariable Long sn,
                                                @RequestBody AccountUpdateRequest request) {
        return ApiResponse.success("更新成功", accountService.update(sn, request));
    }

    @DeleteMapping("/{sn}")
    @PreAuthorize("hasAnyRole('ROLE_LEVEL_3','ROLE_LEVEL_4')")
    public ApiResponse<Void> delete(@PathVariable Long sn) {
        accountService.delete(sn);
        return ApiResponse.success("已刪除", null);
    }

    @PutMapping("/{sn}/password")
    public ApiResponse<Void> changePassword(@PathVariable Long sn,
                                             @Valid @RequestBody ChangePasswordRequest request) {
        accountService.changePassword(sn, request);
        return ApiResponse.success("密碼已變更", null);
    }

    @GetMapping("/companies")
    public ApiResponse<List<Company>> getCompanies(
            @RequestParam(defaultValue = "active") String filter) {
        if ("all".equals(filter)) {
            return ApiResponse.success(accountService.getAllCompanies());
        }
        return ApiResponse.success(accountService.getActiveCompanies());
    }
}
