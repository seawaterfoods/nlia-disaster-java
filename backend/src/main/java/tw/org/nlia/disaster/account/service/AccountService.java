package tw.org.nlia.disaster.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.org.nlia.disaster.account.dto.*;
import tw.org.nlia.disaster.account.repository.CompanyLoginRepository;
import tw.org.nlia.disaster.account.repository.CompanyRepository;
import tw.org.nlia.disaster.common.BusinessException;
import tw.org.nlia.disaster.common.Constants;
import tw.org.nlia.disaster.common.PasswordEncoderUtil;
import tw.org.nlia.disaster.common.ResourceNotFoundException;
import tw.org.nlia.disaster.entity.Company;
import tw.org.nlia.disaster.entity.CompanyLogin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final CompanyLoginRepository companyLoginRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoderUtil passwordEncoderUtil;

    public Page<AccountResponse> search(String email, String name, String cid, Pageable pageable) {
        return companyLoginRepository.search(email, name, cid, pageable)
                .map(this::toResponse);
    }

    public List<AccountResponse> findByCid(String cid) {
        return companyLoginRepository.findByCidOrderByAlevelDescSnAsc(cid).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public AccountResponse findById(Long sn) {
        CompanyLogin login = companyLoginRepository.findById(sn)
                .orElseThrow(() -> new ResourceNotFoundException("帳號", sn));
        return toResponse(login);
    }

    @Transactional
    public AccountResponse create(AccountCreateRequest request) {
        if (companyLoginRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("此 Email 已存在");
        }

        // Validate email domain for non-NLIA companies
        if (!Constants.NLIA_CID.equals(request.getCid())) {
            Company company = companyRepository.findById(request.getCid())
                    .orElseThrow(() -> new ResourceNotFoundException("公司", request.getCid()));
            if (company.getDomain() != null && !company.getDomain().isEmpty()) {
                String emailDomain = request.getEmail().substring(request.getEmail().indexOf("@") + 1);
                if (!emailDomain.equalsIgnoreCase(company.getDomain())) {
                    throw new BusinessException("Email 網域必須為 " + company.getDomain());
                }
            }
        }

        CompanyLogin login = CompanyLogin.builder()
                .cid(request.getCid())
                .email(request.getEmail())
                .password(passwordEncoderUtil.encode(request.getPassword()))
                .name(request.getName())
                .title(request.getTitle())
                .tel(request.getTel())
                .mobile(request.getMobile())
                .email2(request.getEmail2())
                .alevel(request.getAlevel())
                .insurance(request.getInsurance())
                .status(Constants.ACTIVE)
                .adate(LocalDateTime.now())
                .build();

        companyLoginRepository.save(login);
        return toResponse(login);
    }

    @Transactional
    public AccountResponse update(Long sn, AccountUpdateRequest request) {
        CompanyLogin login = companyLoginRepository.findById(sn)
                .orElseThrow(() -> new ResourceNotFoundException("帳號", sn));

        if (request.getName() != null) login.setName(request.getName());
        if (request.getTitle() != null) login.setTitle(request.getTitle());
        if (request.getTel() != null) login.setTel(request.getTel());
        if (request.getMobile() != null) login.setMobile(request.getMobile());
        if (request.getEmail2() != null) login.setEmail2(request.getEmail2());
        if (request.getAlevel() != null) login.setAlevel(request.getAlevel());
        if (request.getInsurance() != null) login.setInsurance(request.getInsurance());
        if (request.getStatus() != null) login.setStatus(request.getStatus());
        login.setUdate(LocalDateTime.now());

        companyLoginRepository.save(login);
        return toResponse(login);
    }

    @Transactional
    public void delete(Long sn) {
        if (!companyLoginRepository.existsById(sn)) {
            throw new ResourceNotFoundException("帳號", sn);
        }
        companyLoginRepository.deleteById(sn);
    }

    @Transactional
    public void changePassword(Long sn, ChangePasswordRequest request) {
        CompanyLogin login = companyLoginRepository.findById(sn)
                .orElseThrow(() -> new ResourceNotFoundException("帳號", sn));

        if (!passwordEncoderUtil.matches(request.getOldPassword(), login.getPassword())) {
            throw new BusinessException("原密碼不正確");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("新密碼與確認密碼不一致");
        }

        login.setPassword(passwordEncoderUtil.encode(request.getNewPassword()));
        login.setUdate(LocalDateTime.now());
        companyLoginRepository.save(login);
    }

    public List<Company> getAllCompanies() {
        return companyRepository.findAllOrderByCid();
    }

    public List<Company> getActiveCompanies() {
        return companyRepository.findByStatusOrderByCidAsc(Constants.ACTIVE);
    }

    private AccountResponse toResponse(CompanyLogin login) {
        String companyName = companyRepository.findById(login.getCid())
                .map(Company::getCname)
                .orElse("");

        return AccountResponse.builder()
                .sn(login.getSn())
                .cid(login.getCid())
                .companyName(companyName)
                .email(login.getEmail())
                .name(login.getName())
                .title(login.getTitle())
                .tel(login.getTel())
                .mobile(login.getMobile())
                .email2(login.getEmail2())
                .alevel(login.getAlevel())
                .insurance(login.getInsurance())
                .status(login.getStatus())
                .lastlogin(login.getLastlogin())
                .ip(login.getIp())
                .build();
    }
}
