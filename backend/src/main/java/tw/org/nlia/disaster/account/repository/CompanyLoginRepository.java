package tw.org.nlia.disaster.account.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.org.nlia.disaster.entity.CompanyLogin;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyLoginRepository extends JpaRepository<CompanyLogin, Long> {

    Optional<CompanyLogin> findByEmail(String email);

    List<CompanyLogin> findByCidOrderByAlevelDescSnAsc(String cid);

    List<CompanyLogin> findByCidAndStatus(String cid, String status);

    @Query("SELECT c FROM CompanyLogin c WHERE " +
           "(:email IS NULL OR c.email LIKE %:email%) AND " +
           "(:name IS NULL OR c.name LIKE %:name%) AND " +
           "(:cid IS NULL OR c.cid = :cid)")
    Page<CompanyLogin> search(@Param("email") String email,
                              @Param("name") String name,
                              @Param("cid") String cid,
                              Pageable pageable);

    long countByCidAndAlevelAndStatus(String cid, Integer alevel, String status);

    @Query("SELECT c FROM CompanyLogin c WHERE c.insurance LIKE %:ins% AND " +
           "(c.email != '' OR c.email2 != '') AND c.status = 'Y' ORDER BY c.cid ASC, c.alevel DESC")
    List<CompanyLogin> findByInsuranceContaining(@Param("ins") String ins);

    boolean existsByEmailAndSnNot(String email, Long sn);

    boolean existsByEmail(String email);
}
