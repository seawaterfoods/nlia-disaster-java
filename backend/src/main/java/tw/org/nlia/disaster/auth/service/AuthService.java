package tw.org.nlia.disaster.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.org.nlia.disaster.account.repository.CompanyLoginRepository;
import tw.org.nlia.disaster.account.repository.CompanyRepository;
import tw.org.nlia.disaster.auth.dto.LoginRequest;
import tw.org.nlia.disaster.auth.dto.LoginResponse;
import tw.org.nlia.disaster.common.BusinessException;
import tw.org.nlia.disaster.common.Constants;
import tw.org.nlia.disaster.common.PasswordEncoderUtil;
import tw.org.nlia.disaster.config.JwtTokenProvider;
import tw.org.nlia.disaster.entity.Company;
import tw.org.nlia.disaster.entity.CompanyLogin;
import tw.org.nlia.disaster.entity.Syslog;
import tw.org.nlia.disaster.syslog.repository.SyslogRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final CompanyLoginRepository companyLoginRepository;
    private final CompanyRepository companyRepository;
    private final SyslogRepository syslogRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoderUtil passwordEncoderUtil;

    @Transactional
    public LoginResponse login(LoginRequest request, String remoteIp) {
        var loginOpt = companyLoginRepository.findByEmail(request.getEmail());

        if (loginOpt.isEmpty() || !passwordEncoderUtil.matches(request.getPassword(), loginOpt.get().getPassword())) {
            // Log failed attempt
            writeSyslog(0L, null, null, request.getEmail(), remoteIp,
                    Constants.ACTION_LOGIN, "N", "帳號密碼錯誤！無法登入！");

            throw new BusinessException("登入失敗！請重新輸入帳號和密碼！");
        }

        CompanyLogin user = loginOpt.get();

        // Auto-upgrade MD5 password to BCrypt
        if (passwordEncoderUtil.isLegacyMd5(user.getPassword())) {
            user.setPassword(passwordEncoderUtil.encode(request.getPassword()));
        }

        // Update last login info
        user.setIp(remoteIp);
        user.setLastlogin(LocalDateTime.now());
        companyLoginRepository.save(user);

        String companyName = companyRepository.findById(user.getCid())
                .map(Company::getCname)
                .orElse("");

        // Log success
        writeSyslog(user.getSn(), user.getCid(), companyName, user.getEmail(), remoteIp,
                Constants.ACTION_LOGIN, "Y", null);

        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getSn(), user.getEmail(), user.getAlevel(), user.getCid());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getSn());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(LoginResponse.UserInfo.builder()
                        .sn(user.getSn())
                        .email(user.getEmail())
                        .name(user.getName())
                        .title(user.getTitle())
                        .cid(user.getCid())
                        .companyName(companyName)
                        .level(user.getAlevel())
                        .insurance(user.getInsurance())
                        .build())
                .build();
    }

    @Transactional
    public void logout(Long userSn, String remoteIp) {
        companyLoginRepository.findById(userSn).ifPresent(user -> {
            String companyName = companyRepository.findById(user.getCid())
                    .map(Company::getCname).orElse("");
            writeSyslog(user.getSn(), user.getCid(), companyName, user.getEmail(), remoteIp,
                    Constants.ACTION_LOGOUT, "Y", null);
        });
    }

    @Transactional
    public String forgotPassword(String email, String remoteIp) {
        var loginOpt = companyLoginRepository.findByEmail(email.toLowerCase().trim());

        if (loginOpt.isEmpty()) {
            throw new BusinessException("您的帳號輸入有誤，或查無此帳號");
        }

        CompanyLogin user = loginOpt.get();
        String tempPassword = passwordEncoderUtil.generateTempPassword(8);
        user.setPassword(passwordEncoderUtil.encode(tempPassword));
        companyLoginRepository.save(user);

        String companyName = companyRepository.findById(user.getCid())
                .map(Company::getCname).orElse("");
        writeSyslog(user.getSn(), user.getCid(), companyName, email, remoteIp,
                Constants.ACTION_RESET_PWD, "Y", null);

        return tempPassword;
    }

    private void writeSyslog(Long adminsn, String cid, String company, String loginid,
                             String fromip, String action, String mstatus, String detail) {
        Syslog syslog = Syslog.builder()
                .adminsn(adminsn)
                .cid(cid)
                .company(company)
                .loginid(loginid)
                .fromip(fromip)
                .action(action)
                .mstatus(mstatus)
                .adate(System.currentTimeMillis() / 1000)
                .detail(detail)
                .build();
        syslogRepository.save(syslog);
    }
}
