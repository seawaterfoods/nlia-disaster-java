package tw.org.nlia.disaster.closs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.org.nlia.disaster.entity.NdReportCloss;

import java.util.List;

@Repository
public interface NdReportClossRepository extends JpaRepository<NdReportCloss, Long> {

    List<NdReportCloss> findByNdsnAndCidAndShowStatusOrderBySnAsc(Long ndsn, String cid, String showStatus);

    List<NdReportCloss> findByNdsnAndCidAndShowStatusOrderBySnDesc(Long ndsn, String cid, String showStatus);

    List<NdReportCloss> findByNdsnAndShowStatus(Long ndsn, String showStatus);
}
