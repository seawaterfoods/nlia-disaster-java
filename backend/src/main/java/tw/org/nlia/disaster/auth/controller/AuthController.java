package tw.org.nlia.disaster.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tw.org.nlia.disaster.auth.dto.LoginRequest;
import tw.org.nlia.disaster.auth.dto.LoginResponse;
import tw.org.nlia.disaster.auth.service.AuthService;
import tw.org.nlia.disaster.common.ApiResponse;

import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "認證", description = "登入、登出、忘記密碼")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "登入", description = "使用帳號密碼登入系統")
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                            HttpServletRequest httpRequest) {
        LoginResponse response = authService.login(request, httpRequest.getRemoteAddr());
        return ApiResponse.success("登入成功", response);
    }

    @Operation(summary = "登出", description = "登出當前使用者")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest httpRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long userSn) {
            authService.logout(userSn, httpRequest.getRemoteAddr());
        }
        return ApiResponse.success("已登出", null);
    }

    @Operation(summary = "忘記密碼", description = "寄送新密碼至指定信箱")
    @PostMapping("/forgot-password")
    public ApiResponse<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request,
                                                            HttpServletRequest httpRequest) {
        String email = request.get("email");
        authService.forgotPassword(email, httpRequest.getRemoteAddr());
        return ApiResponse.success("新密碼已寄送至您的信箱", Map.of("message", "新密碼已寄送至 " + email));
    }
}
