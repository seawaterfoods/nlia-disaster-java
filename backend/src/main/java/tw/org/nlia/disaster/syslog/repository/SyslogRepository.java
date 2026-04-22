package tw.org.nlia.disaster.syslog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.org.nlia.disaster.entity.Syslog;

@Repository
public interface SyslogRepository extends JpaRepository<Syslog, Long> {

    @Query("SELECT s FROM Syslog s WHERE " +
           "(:adminsn IS NULL OR s.adminsn = :adminsn) AND " +
           "(:cid IS NULL OR s.cid = :cid) AND " +
           "(:action IS NULL OR s.action = :action) " +
           "ORDER BY s.sn DESC")
    Page<Syslog> findFiltered(@Param("adminsn") Long adminsn,
                              @Param("cid") String cid,
                              @Param("action") String action,
                              Pageable pageable);
}
