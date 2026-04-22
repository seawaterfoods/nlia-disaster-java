package tw.org.nlia.disaster.alert.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.org.nlia.disaster.entity.NdAlert;

import java.util.List;
import java.util.Optional;

@Repository
public interface NdAlertRepository extends JpaRepository<NdAlert, Long> {

    List<NdAlert> findByNdsnOrderByAdateDesc(Long ndsn);

    Page<NdAlert> findAllByOrderBySnDesc(Pageable pageable);

    Optional<NdAlert> findByAdminsnAndNdsn(Long adminsn, Long ndsn);
}
