package tw.org.nlia.disaster.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.org.nlia.disaster.account.repository.CompanyRepository;
import tw.org.nlia.disaster.alert.service.AlertService;
import tw.org.nlia.disaster.common.Constants;
import tw.org.nlia.disaster.common.ResourceNotFoundException;
import tw.org.nlia.disaster.entity.Company;
import tw.org.nlia.disaster.entity.NdReportDetail;
import tw.org.nlia.disaster.entity.NdReportMain;
import tw.org.nlia.disaster.report.dto.*;
import tw.org.nlia.disaster.report.repository.NdReportDetailRepository;
import tw.org.nlia.disaster.report.repository.NdReportMainRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final NdReportMainRepository reportMainRepository;
    private final NdReportDetailRepository reportDetailRepository;
    private final CompanyRepository companyRepository;
    private final AlertService alertService;

    // ==================== Report Main ====================

    /**
     * Auto-create report main record (matches PHP check_rpt_main)
     * Called when a company views the dashboard for an active disaster
     */
    @Transactional
    public NdReportMain ensureReportMain(Long ndsn, String cid, String author) {
        var existing = reportMainRepository.findByNdsnAndCid(ndsn, cid);
        if (existing.isPresent()) {
            return existing.get();
        }

        NdReportMain main = NdReportMain.builder()
                .ndsn(ndsn)
                .cid(cid)
                .nd1(Constants.STATUS_NOT_REPORTED)
                .nd2(Constants.STATUS_NOT_REPORTED)
                .nd3(Constants.STATUS_NOT_REPORTED)
                .nd4(Constants.STATUS_NOT_REPORTED)
                .nd5(Constants.STATUS_NOT_REPORTED)
                .closs(Constants.STATUS_NOT_REPORTED)
                .author(author)
                .adate(LocalDateTime.now())
                .build();

        return reportMainRepository.save(main);
    }

    public List<ReportMainResponse> findMainByDisaster(Long ndsn) {
        return reportMainRepository.findByNdsnOrderByCidAsc(ndsn).stream()
                .map(this::toMainResponse)
                .collect(Collectors.toList());
    }

    public ReportMainResponse findMainByDisasterAndCompany(Long ndsn, String cid) {
        return reportMainRepository.findByNdsnAndCid(ndsn, cid)
                .map(this::toMainResponse)
                .orElseThrow(() -> new ResourceNotFoundException("通報主檔", ndsn + "-" + cid));
    }

    @Transactional
    public ReportMainResponse updateMainStatus(Long ndsn, String cid, String field, String status) {
        NdReportMain main = reportMainRepository.findByNdsnAndCid(ndsn, cid)
                .orElseThrow(() -> new ResourceNotFoundException("通報主檔", ndsn + "-" + cid));

        switch (field) {
            case "nd1" -> main.setNd1(status);
            case "nd2" -> main.setNd2(status);
            case "nd3" -> main.setNd3(status);
            case "nd4" -> main.setNd4(status);
            case "nd5" -> main.setNd5(status);
            case "closs" -> main.setCloss(status);
        }
        main.setUdate(LocalDateTime.now());
        reportMainRepository.save(main);
        return toMainResponse(main);
    }

    // ==================== Report Detail ====================

    public List<ReportDetailResponse> findDetailsByDisasterAndCompany(Long ndsn, String cid) {
        return reportDetailRepository.findByNdsnAndCidAndShowStatusOrderBySnAsc(ndsn, cid, "Y").stream()
                .map(this::toDetailResponse)
                .collect(Collectors.toList());
    }

    public List<ReportDetailResponse> findDetailsByDisasterAndCompanyAndType(Long ndsn, String cid, String hname) {
        return reportDetailRepository.findByNdsnAndCidAndHnameAndShowStatus(ndsn, cid, hname, "Y").stream()
                .map(this::toDetailResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReportDetailResponse createDetail(ReportDetailRequest request, Long adminsn) {
        NdReportDetail detail = NdReportDetail.builder()
                .ndsn(request.getNdsn())
                .cid(request.getCid())
                .bid(request.getBid())
                .zip(request.getZip())
                .city(request.getCity())
                .area(request.getArea())
                .hname(request.getHname())
                .bname(request.getBname())
                .pname(request.getPname())
                .ndTypeSn(request.getNdTypeSn())
                .ndDate(request.getNdDate())
                .preCost(request.getPreCost())
                .preInum(request.getPreInum())
                .preDnum(request.getPreDnum())
                .commited(request.getCommited())
                .pending(request.getPending())
                .prepay(request.getPrepay())
                .close(request.getClose())
                .memo(request.getMemo())
                .showStatus("Y")
                .adminsn(adminsn)
                .addSn(adminsn)
                .adate(LocalDateTime.now())
                .build();

        reportDetailRepository.save(detail);

        // Check amount threshold alert (matches PHP nd_alert3)
        alertService.checkAmountAlert(request.getNdsn(), request.getCid(), request.getPreCost());

        return toDetailResponse(detail);
    }

    @Transactional
    public ReportDetailResponse updateDetail(Long sn, ReportDetailRequest request, Long adminsn) {
        NdReportDetail detail = reportDetailRepository.findById(sn)
                .orElseThrow(() -> new ResourceNotFoundException("災損明細", sn));

        if (request.getBid() != null) detail.setBid(request.getBid());
        if (request.getZip() != null) detail.setZip(request.getZip());
        if (request.getCity() != null) detail.setCity(request.getCity());
        if (request.getArea() != null) detail.setArea(request.getArea());
        if (request.getHname() != null) detail.setHname(request.getHname());
        if (request.getBname() != null) detail.setBname(request.getBname());
        if (request.getPname() != null) detail.setPname(request.getPname());
        if (request.getNdTypeSn() != null) detail.setNdTypeSn(request.getNdTypeSn());
        if (request.getNdDate() != null) detail.setNdDate(request.getNdDate());
        if (request.getPreCost() != null) detail.setPreCost(request.getPreCost());
        if (request.getPreInum() != null) detail.setPreInum(request.getPreInum());
        if (request.getPreDnum() != null) detail.setPreDnum(request.getPreDnum());
        if (request.getCommited() != null) detail.setCommited(request.getCommited());
        if (request.getPending() != null) detail.setPending(request.getPending());
        if (request.getPrepay() != null) detail.setPrepay(request.getPrepay());
        if (request.getClose() != null) detail.setClose(request.getClose());
        if (request.getMemo() != null) detail.setMemo(request.getMemo());
        detail.setAdminsn(adminsn);
        detail.setUdate(LocalDateTime.now());

        reportDetailRepository.save(detail);
        return toDetailResponse(detail);
    }

    @Transactional
    public void deleteDetail(Long sn, Long adminsn) {
        NdReportDetail detail = reportDetailRepository.findById(sn)
                .orElseThrow(() -> new ResourceNotFoundException("災損明細", sn));
        // Soft delete (matches PHP ajax_proc.php del_obj behavior)
        detail.setShowStatus("N");
        detail.setDelSn(adminsn);
        detail.setDelDate(LocalDateTime.now());
        reportDetailRepository.save(detail);
    }

    /**
     * Get sum of preCost for a disaster+company (for amount alert comparison)
     */
    public BigDecimal sumPreCostByDisasterAndCompany(Long ndsn, String cid) {
        BigDecimal sum = reportDetailRepository.sumPreCostByNdsnAndCid(ndsn, cid);
        return sum != null ? sum : BigDecimal.ZERO;
    }

    // ==================== Mappers ====================

    private ReportMainResponse toMainResponse(NdReportMain main) {
        String companyName = companyRepository.findById(main.getCid())
                .map(Company::getCname).orElse("");
        return ReportMainResponse.builder()
                .sn(main.getSn())
                .ndsn(main.getNdsn())
                .cid(main.getCid())
                .companyName(companyName)
                .nd1(main.getNd1())
                .nd2(main.getNd2())
                .nd3(main.getNd3())
                .nd4(main.getNd4())
                .nd5(main.getNd5())
                .closs(main.getCloss())
                .author(main.getAuthor())
                .adate(main.getAdate())
                .udate(main.getUdate())
                .build();
    }

    private ReportDetailResponse toDetailResponse(NdReportDetail d) {
        return ReportDetailResponse.builder()
                .sn(d.getSn())
                .ndsn(d.getNdsn())
                .cid(d.getCid())
                .bid(d.getBid())
                .zip(d.getZip())
                .city(d.getCity())
                .area(d.getArea())
                .hname(d.getHname())
                .bname(d.getBname())
                .pname(d.getPname())
                .ndTypeSn(d.getNdTypeSn())
                .ndDate(d.getNdDate())
                .preCost(d.getPreCost())
                .preInum(d.getPreInum())
                .preDnum(d.getPreDnum())
                .commited(d.getCommited())
                .pending(d.getPending())
                .prepay(d.getPrepay())
                .close(d.getClose())
                .memo(d.getMemo())
                .showStatus(d.getShowStatus())
                .adate(d.getAdate())
                .udate(d.getUdate())
                .build();
    }
}
