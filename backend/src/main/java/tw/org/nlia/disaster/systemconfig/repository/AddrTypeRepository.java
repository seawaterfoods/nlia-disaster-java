package tw.org.nlia.disaster.systemconfig.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tw.org.nlia.disaster.entity.AddrType;

import java.util.List;

@Repository
public interface AddrTypeRepository extends JpaRepository<AddrType, String> {

    List<AddrType> findByCsnOrderByAsnAsc(String csn);

    List<AddrType> findByCname(String cname);

    @Query("SELECT DISTINCT a.cname FROM AddrType a ORDER BY a.cname ASC")
    List<String> findDistinctCnames();

    @Query("SELECT DISTINCT a.csn FROM AddrType a ORDER BY a.csn ASC")
    List<String> findDistinctCsn();

    @Query("SELECT a.cname FROM AddrType a WHERE a.csn = :csn")
    String findCnameByCsn(String csn);
}
