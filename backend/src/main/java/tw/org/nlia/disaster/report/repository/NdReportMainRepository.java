package tw.org.nlia.disaster.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.org.nlia.disaster.entity.NdReportMain;

import java.util.List;
import java.util.Optional;

@Repository
public interface NdReportMainRepository extends JpaRepository<NdReportMain, Long> {

    Optional<NdReportMain> findByNdsnAndCid(Long ndsn, String cid);

    List<NdReportMain> findByNdsn(Long ndsn);

    boolean existsByNdsnAndCid(Long ndsn, String cid);
}
