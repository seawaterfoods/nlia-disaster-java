package tw.org.nlia.disaster.systemconfig.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.org.nlia.disaster.entity.NdType;

import java.util.List;

@Repository
public interface NdTypeRepository extends JpaRepository<NdType, Long> {

    List<NdType> findByHnameOrderByBnameAsc(String hname);

    List<NdType> findByBnameAndPnameNotOrderByPnameAsc(String bname, String pnameNot);

    @Query("SELECT DISTINCT n.bname FROM NdType n WHERE n.hname = :hname ORDER BY n.bname ASC")
    List<String> findDistinctBnameByHname(@Param("hname") String hname);

    @Query("SELECT DISTINCT n.hname FROM NdType n ORDER BY n.hsort ASC")
    List<String> findDistinctHnames();
}
