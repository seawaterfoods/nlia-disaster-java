package tw.org.nlia.disaster.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tw.org.nlia.disaster.account.repository.CompanyLoginRepository;
import tw.org.nlia.disaster.account.repository.CompanyRepository;
import tw.org.nlia.disaster.auth.dto.LoginRequest;
import tw.org.nlia.disaster.auth.dto.LoginResponse;
import tw.org.nlia.disaster.common.BusinessException;
import tw.org.nlia.disaster.common.PasswordEncoderUtil;
import tw.org.nlia.disaster.config.JwtTokenProvider;
import tw.org.nlia.disaster.entity.Company;
import tw.org.nlia.disaster.entity.CompanyLogin;
import tw.org.nlia.disaster.syslog.repository.SyslogRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private CompanyLoginRepository companyLoginRepository;
    @Mock private CompanyRepository companyRepository;
    @Mock private SyslogRepository syslogRepository;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private PasswordEncoderUtil passwordEncoderUtil;

    @InjectMocks
    private AuthService authService;

    private CompanyLogin testUser;
    private Company testCompany;

    @BeforeEach
    void setUp() {
        testUser = CompanyLogin.builder()
                .sn(1L)
                .cid("01")
                .email("test@example.com")
                .password("$2a$10$hashedPassword")
                .name("測試用戶")
                .title("經理")
                .alevel(1)
                .insurance("1,2")
                .status("Y")
                .build();

        testCompany = Company.builder()
                .cid("01")
                .cname("測試保險公司")
                .build();
    }

    @Test
    void login_success() {
        when(companyLoginRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoderUtil.matches("password123", testUser.getPassword())).thenReturn(true);
        when(passwordEncoderUtil.isLegacyMd5(testUser.getPassword())).thenReturn(false);
        when(companyRepository.findById("01")).thenReturn(Optional.of(testCompany));
        when(jwtTokenProvider.generateAccessToken(1L, "test@example.com", 1, "01")).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(1L)).thenReturn("refresh-token");

        LoginResponse response = authService.login(
                new LoginRequest("test@example.com", "password123"), "127.0.0.1");

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("測試用戶", response.getUser().getName());
        assertEquals("測試保險公司", response.getUser().getCompanyName());
        verify(syslogRepository, times(1)).save(any());
    }

    @Test
    void login_failure_wrongPassword() {
        when(companyLoginRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoderUtil.matches("wrong", testUser.getPassword())).thenReturn(false);

        assertThrows(BusinessException.class, () ->
                authService.login(new LoginRequest("test@example.com", "wrong"), "127.0.0.1"));

        verify(syslogRepository, times(1)).save(any());
    }

    @Test
    void login_failure_userNotFound() {
        when(companyLoginRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () ->
                authService.login(new LoginRequest("nonexistent@example.com", "pass"), "127.0.0.1"));
    }

    @Test
    void login_autoUpgradeMd5Password() {
        testUser.setPassword("e10adc3949ba59abbe56e057f20f883e");
        when(companyLoginRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoderUtil.matches("123456", testUser.getPassword())).thenReturn(true);
        when(passwordEncoderUtil.isLegacyMd5(testUser.getPassword())).thenReturn(true);
        when(passwordEncoderUtil.encode("123456")).thenReturn("$2a$10$newBcryptHash");
        when(companyRepository.findById("01")).thenReturn(Optional.of(testCompany));
        when(jwtTokenProvider.generateAccessToken(anyLong(), anyString(), anyInt(), anyString())).thenReturn("token");
        when(jwtTokenProvider.generateRefreshToken(anyLong())).thenReturn("refresh");

        authService.login(new LoginRequest("test@example.com", "123456"), "127.0.0.1");

        assertEquals("$2a$10$newBcryptHash", testUser.getPassword());
    }

    @Test
    void forgotPassword_success() {
        when(companyLoginRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoderUtil.generateTempPassword(8)).thenReturn("TempPw12");
        when(passwordEncoderUtil.encode("TempPw12")).thenReturn("$2a$10$encodedTempPw");
        when(companyRepository.findById("01")).thenReturn(Optional.of(testCompany));

        String result = authService.forgotPassword("test@example.com", "127.0.0.1");

        assertEquals("TempPw12", result);
        verify(companyLoginRepository).save(testUser);
    }

    @Test
    void forgotPassword_userNotFound() {
        when(companyLoginRepository.findByEmail("none@example.com")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () ->
                authService.forgotPassword("none@example.com", "127.0.0.1"));
    }
}
