package tw.org.nlia.disaster.closs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.org.nlia.disaster.closs.repository.NdReportClossRepository;
import tw.org.nlia.disaster.common.ResourceNotFoundException;
import tw.org.nlia.disaster.entity.NdReportCloss;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClossService {

    private final NdReportClossRepository clossRepository;

    public List<NdReportCloss> findByDisasterAndCompany(Long ndsn, String cid) {
        return clossRepository.findByNdsnAndCidAndShowStatusOrderBySnAsc(ndsn, cid, "Y");
    }

    @Transactional
    public NdReportCloss create(NdReportCloss closs, Long adminsn) {
        closs.setShowStatus("Y");
        closs.setAdminsn(adminsn);
        closs.setAdate(LocalDateTime.now());
        return clossRepository.save(closs);
    }

    @Transactional
    public NdReportCloss update(Long sn, NdReportCloss request, Long adminsn) {
        NdReportCloss closs = clossRepository.findById(sn)
                .orElseThrow(() -> new ResourceNotFoundException("自有房舍", sn));

        if (request.getUname() != null) closs.setUname(request.getUname());
        if (request.getZip() != null) closs.setZip(request.getZip());
        if (request.getS1() != null) closs.setS1(request.getS1());
        if (request.getS2() != null) closs.setS2(request.getS2());
        if (request.getS3() != null) closs.setS3(request.getS3());
        if (request.getS4() != null) closs.setS4(request.getS4());
        if (request.getS5() != null) closs.setS5(request.getS5());
        if (request.getInum() != null) closs.setInum(request.getInum());
        if (request.getDnum() != null) closs.setDnum(request.getDnum());
        if (request.getMemo() != null) closs.setMemo(request.getMemo());
        closs.setAdminsn(adminsn);
        closs.setUdate(LocalDateTime.now());

        return clossRepository.save(closs);
    }

    @Transactional
    public void delete(Long sn) {
        NdReportCloss closs = clossRepository.findById(sn)
                .orElseThrow(() -> new ResourceNotFoundException("自有房舍", sn));
        closs.setShowStatus("N");
        clossRepository.save(closs);
    }
}
