package tw.org.nlia.disaster.report.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.org.nlia.disaster.entity.NdReportDetail;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface NdReportDetailRepository extends JpaRepository<NdReportDetail, Long> {

    @Query("SELECT d FROM NdReportDetail d WHERE d.ndsn = :ndsn AND d.cid = :cid AND d.showStatus = :showStatus ORDER BY d.sn ASC")
    List<NdReportDetail> findByNdsnAndCidAndShowStatusOrderBySnAsc(@Param("ndsn") Long ndsn, @Param("cid") String cid, @Param("showStatus") String showStatus);

    List<NdReportDetail> findByNdsnAndCidAndHnameAndShowStatus(Long ndsn, String cid, String hname, String showStatus);

    List<NdReportDetail> findByNdsnAndCidAndShowStatusOrderBySnDesc(Long ndsn, String cid, String showStatus);

    @Query("SELECT COALESCE(SUM(d.preCost), 0) FROM NdReportDetail d WHERE d.ndsn = :ndsn AND d.cid = :cid AND d.showStatus = 'Y'")
    BigDecimal sumPreCostByNdsnAndCid(@Param("ndsn") Long ndsn, @Param("cid") String cid);

    Page<NdReportDetail> findByNdsnAndCidAndShowStatus(Long ndsn, String cid, String showStatus, Pageable pageable);

    @Query("SELECT COUNT(d) FROM NdReportDetail d WHERE d.ndsn = :ndsn AND d.cid = :cid AND d.showStatus = 'Y'")
    long countByNdsnAndCid(@Param("ndsn") Long ndsn, @Param("cid") String cid);

    @Query("SELECT COUNT(d) FROM NdReportDetail d WHERE d.ndsn = :ndsn AND d.cid = :cid AND d.showStatus = 'Y' AND d.hname = :hname")
    long countByNdsnAndCidAndHname(@Param("ndsn") Long ndsn, @Param("cid") String cid, @Param("hname") String hname);

    @Query("SELECT COALESCE(SUM(d.preCost), 0) FROM NdReportDetail d WHERE d.ndsn = :ndsn AND d.cid = :cid AND d.showStatus = 'Y' AND d.hname = :hname")
    BigDecimal sumPreCostByNdsnAndCidAndHname(@Param("ndsn") Long ndsn, @Param("cid") String cid, @Param("hname") String hname);

    @Query("SELECT d FROM NdReportDetail d WHERE d.ndsn = :ndsn AND d.showStatus = 'Y'")
    List<NdReportDetail> findAllActiveByNdsn(@Param("ndsn") Long ndsn);

    @Query("SELECT COUNT(d) FROM NdReportDetail d WHERE d.ndsn = :ndsn AND d.showStatus = 'Y' AND d.close = :close")
    long countByNdsnAndClose(@Param("ndsn") Long ndsn, @Param("close") String close);

    boolean existsByNdsn(Long ndsn);

    /** Soft-delete all details for a disaster+company (matches PHP behavior when nd='N') */
    @Modifying
    @Query("UPDATE NdReportDetail d SET d.showStatus = 'N', d.delDate = CURRENT_TIMESTAMP WHERE d.ndsn = :ndsn AND d.cid = :cid AND d.showStatus = 'Y'")
    int softDeleteByNdsnAndCid(@Param("ndsn") Long ndsn, @Param("cid") String cid);
}
