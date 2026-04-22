package tw.org.nlia.disaster.disaster.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.org.nlia.disaster.common.ResourceNotFoundException;
import tw.org.nlia.disaster.disaster.dto.DisasterRequest;
import tw.org.nlia.disaster.disaster.dto.DisasterResponse;
import tw.org.nlia.disaster.disaster.repository.DisasterRepository;
import tw.org.nlia.disaster.entity.Disaster;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DisasterService {

    private final DisasterRepository disasterRepository;

    /**
     * Generate disaster ID: YYYYMMDD + 3-digit zero-padded sequence
     * E.g., first disaster on 2024-01-15 → "20240115001"
     */
    private String generateDisasterId() {
        String prefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String maxId = disasterRepository.findMaxIdByPrefix(prefix);

        int nextSeq = 1;
        if (maxId != null && maxId.length() > 8) {
            nextSeq = Integer.parseInt(maxId.substring(8)) + 1;
        }
        return prefix + String.format("%03d", nextSeq);
    }

    public List<DisasterResponse> findAll() {
        return disasterRepository.findAllByOrderBySnDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<DisasterResponse> findActive() {
        return disasterRepository.findByShowStatusOrderBySnDesc("Y").stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public DisasterResponse findById(Long sn) {
        return toResponse(disasterRepository.findById(sn)
                .orElseThrow(() -> new ResourceNotFoundException("災害事件", sn)));
    }

    @Transactional
    public DisasterResponse create(DisasterRequest request, String authorCid, Long authorSn) {
        String disasterId = generateDisasterId();

        Disaster disaster = Disaster.builder()
                .id(disasterId)
                .title(request.getTitle())
                .content(request.getContent())
                .ddate(request.getDdate())
                .adate(request.getAdate())
                .sdate(request.getSdate())
                .vdate(request.getVdate())
                .claimDate(request.getClaimDate())
                .claimValid(request.getClaimValid())
                .allowIns(request.getAllowIns())
                .emailNotice(request.getEmailNotice())
                .reason(request.getReason())
                .df(request.getDf())
                .showStatus(request.getShowStatus() != null ? request.getShowStatus() : "Y")
                .qstatus(request.getQstatus())
                .claimAlert(request.getClaimAlert())
                .authorCid(authorCid)
                .authorSn(authorSn)
                .build();

        disasterRepository.save(disaster);
        return toResponse(disaster);
    }

    @Transactional
    public DisasterResponse update(Long sn, DisasterRequest request) {
        Disaster disaster = disasterRepository.findById(sn)
                .orElseThrow(() -> new ResourceNotFoundException("災害事件", sn));

        if (request.getTitle() != null) disaster.setTitle(request.getTitle());
        if (request.getContent() != null) disaster.setContent(request.getContent());
        if (request.getDdate() != null) disaster.setDdate(request.getDdate());
        if (request.getAdate() != null) disaster.setAdate(request.getAdate());
        if (request.getSdate() != null) disaster.setSdate(request.getSdate());
        if (request.getVdate() != null) disaster.setVdate(request.getVdate());
        if (request.getClaimDate() != null) disaster.setClaimDate(request.getClaimDate());
        if (request.getClaimValid() != null) disaster.setClaimValid(request.getClaimValid());
        if (request.getAllowIns() != null) disaster.setAllowIns(request.getAllowIns());
        if (request.getEmailNotice() != null) disaster.setEmailNotice(request.getEmailNotice());
        if (request.getReason() != null) disaster.setReason(request.getReason());
        if (request.getDf() != null) disaster.setDf(request.getDf());
        if (request.getShowStatus() != null) disaster.setShowStatus(request.getShowStatus());
        if (request.getQstatus() != null) disaster.setQstatus(request.getQstatus());
        if (request.getClaimAlert() != null) disaster.setClaimAlert(request.getClaimAlert());

        disasterRepository.save(disaster);
        return toResponse(disaster);
    }

    @Transactional
    public void delete(Long sn) {
        if (!disasterRepository.existsById(sn)) {
            throw new ResourceNotFoundException("災害事件", sn);
        }
        disasterRepository.deleteById(sn);
    }

    private DisasterResponse toResponse(Disaster d) {
        return DisasterResponse.builder()
                .sn(d.getSn())
                .id(d.getId())
                .title(d.getTitle())
                .content(d.getContent())
                .ddate(d.getDdate())
                .adate(d.getAdate())
                .sdate(d.getSdate())
                .vdate(d.getVdate())
                .claimDate(d.getClaimDate())
                .claimValid(d.getClaimValid())
                .allowIns(d.getAllowIns())
                .emailNotice(d.getEmailNotice())
                .reason(d.getReason())
                .df(d.getDf())
                .showStatus(d.getShowStatus())
                .qstatus(d.getQstatus())
                .qdate(d.getQdate())
                .claimAlert(d.getClaimAlert())
                .authorCid(d.getAuthorCid())
                .authorSn(d.getAuthorSn())
                .build();
    }
}
