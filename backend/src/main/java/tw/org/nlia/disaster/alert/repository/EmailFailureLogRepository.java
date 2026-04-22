package tw.org.nlia.disaster.alert.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.org.nlia.disaster.entity.EmailFailureLog;

import java.util.List;

@Repository
public interface EmailFailureLogRepository extends JpaRepository<EmailFailureLog, Long> {

    @Query("SELECT e FROM EmailFailureLog e WHERE e.resolved = 'N' AND e.triggeredBySn = :userSn ORDER BY e.adate DESC")
    List<EmailFailureLog> findUnresolvedByUser(@Param("userSn") Long userSn);

    @Query("SELECT e FROM EmailFailureLog e WHERE e.resolved = 'N' ORDER BY e.adate DESC")
    List<EmailFailureLog> findAllUnresolved();

    @Modifying
    @Query("UPDATE EmailFailureLog e SET e.resolved = 'Y', e.resolvedDate = CURRENT_TIMESTAMP WHERE e.sn = :sn")
    int markResolved(@Param("sn") Long sn);
}
