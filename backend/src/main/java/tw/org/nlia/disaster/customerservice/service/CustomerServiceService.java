package tw.org.nlia.disaster.customerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.org.nlia.disaster.common.ResourceNotFoundException;
import tw.org.nlia.disaster.customerservice.repository.CustomerServiceColumnRepository;
import tw.org.nlia.disaster.customerservice.repository.CustomerServiceDataRepository;
import tw.org.nlia.disaster.entity.CustomerServiceColumn;
import tw.org.nlia.disaster.entity.CustomerServiceData;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceService {

    private final CustomerServiceColumnRepository columnRepository;
    private final CustomerServiceDataRepository dataRepository;

    // ==================== Columns ====================
    public List<CustomerServiceColumn> findColumnsByDisaster(Long ndsn) {
        return columnRepository.findByNdsnOrderBySortAsc(ndsn);
    }

    @Transactional
    public CustomerServiceColumn createColumn(CustomerServiceColumn column) {
        return columnRepository.save(column);
    }

    @Transactional
    public void deleteColumn(Long sn) {
        columnRepository.deleteById(sn);
    }

    // ==================== Data ====================
    public List<CustomerServiceData> findDataByDisasterAndCompany(Long ndsn, String cid) {
        return dataRepository.findByNdsnAndCidOrderByColumnSnAsc(ndsn, cid);
    }

    @Transactional
    public CustomerServiceData saveData(CustomerServiceData data, Long adminsn) {
        data.setAdminsn(adminsn);
        data.setUdate(LocalDateTime.now());
        return dataRepository.save(data);
    }

    @Transactional
    public void deleteData(Long sn) {
        dataRepository.deleteById(sn);
    }
}
