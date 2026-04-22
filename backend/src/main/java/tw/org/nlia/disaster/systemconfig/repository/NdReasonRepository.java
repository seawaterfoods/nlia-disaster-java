package tw.org.nlia.disaster.systemconfig.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.org.nlia.disaster.entity.NdReason;

import java.util.List;

@Repository
public interface NdReasonRepository extends JpaRepository<NdReason, Long> {

    List<NdReason> findAllByOrderBySnAsc();
}
