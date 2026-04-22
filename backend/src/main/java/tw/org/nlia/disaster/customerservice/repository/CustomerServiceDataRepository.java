package tw.org.nlia.disaster.customerservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.org.nlia.disaster.entity.CustomerServiceData;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerServiceDataRepository extends JpaRepository<CustomerServiceData, Long> {

    List<CustomerServiceData> findByNdsnAndCid(Long ndsn, String cid);

    Optional<CustomerServiceData> findByNdsnAndCidAndColumnSn(Long ndsn, String cid, Long columnSn);

    List<CustomerServiceData> findByNdsn(Long ndsn);
}
