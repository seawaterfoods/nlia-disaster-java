package tw.org.nlia.disaster.disaster.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.org.nlia.disaster.entity.Disaster;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DisasterRepository extends JpaRepository<Disaster, Long> {

    Page<Disaster> findAllByOrderBySnDesc(Pageable pageable);

    @Query("SELECT d FROM Disaster d WHERE d.adate <= :today AND d.vdate >= :today ORDER BY d.sn DESC")
    List<Disaster> findActiveDisasters(@Param("today") LocalDate today);

    @Query("SELECT d FROM Disaster d WHERE d.showStatus = 'Y' ORDER BY d.sn DESC")
    List<Disaster> findVisible();

    @Query("SELECT MAX(d.id) FROM Disaster d WHERE d.id LIKE :prefix%")
    String findMaxIdByPrefix(@Param("prefix") String prefix);
}
