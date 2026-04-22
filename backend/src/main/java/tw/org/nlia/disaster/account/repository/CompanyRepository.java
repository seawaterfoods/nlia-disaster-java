package tw.org.nlia.disaster.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tw.org.nlia.disaster.entity.Company;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {

    List<Company> findByStatusOrderByCidAsc(String status);

    @Query("SELECT c FROM Company c ORDER BY c.cid ASC")
    List<Company> findAllOrderByCid();
}
