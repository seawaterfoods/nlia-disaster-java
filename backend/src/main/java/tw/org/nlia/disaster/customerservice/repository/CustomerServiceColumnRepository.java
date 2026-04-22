package tw.org.nlia.disaster.customerservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.org.nlia.disaster.entity.CustomerServiceColumn;

import java.util.List;

@Repository
public interface CustomerServiceColumnRepository extends JpaRepository<CustomerServiceColumn, Long> {

    List<CustomerServiceColumn> findByNdsnOrderBySortAsc(Long ndsn);
}
