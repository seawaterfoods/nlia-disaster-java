package tw.org.nlia.disaster.systemconfig.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.org.nlia.disaster.common.ResourceNotFoundException;
import tw.org.nlia.disaster.entity.*;
import tw.org.nlia.disaster.systemconfig.repository.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemConfigService {

    private final SysConfigRepository sysConfigRepository;
    private final AddrTypeRepository addrTypeRepository;
    private final NdTypeRepository ndTypeRepository;
    private final NdReasonRepository ndReasonRepository;

    // ==================== SysConfig ====================
    public List<SysConfig> getAllConfigs() {
        return sysConfigRepository.findAll();
    }

    public SysConfig getConfigByKey(String id) {
        return sysConfigRepository.findByConfigId(id)
                .orElseThrow(() -> new ResourceNotFoundException("系統設定", id));
    }

    @Transactional
    public SysConfig updateConfig(String id, String content) {
        SysConfig config = sysConfigRepository.findByConfigId(id)
                .orElseThrow(() -> new ResourceNotFoundException("系統設定", id));
        config.setContent(content);
        return sysConfigRepository.save(config);
    }

    // ==================== AddrType ====================
    public List<AddrType> getAllAddresses() {
        return addrTypeRepository.findAll();
    }

    public List<String> getDistinctCities() {
        return addrTypeRepository.findDistinctCnames();
    }

    public List<AddrType> getAreasByCity(String cname) {
        return addrTypeRepository.findByCname(cname);
    }

    public AddrType getAddressByZip(String asn) {
        return addrTypeRepository.findById(asn)
                .orElseThrow(() -> new ResourceNotFoundException("地址", asn));
    }

    // ==================== NdType ====================
    public List<NdType> getAllProducts() {
        return ndTypeRepository.findAllByOrderByHsortAscSnAsc();
    }

    public List<String> getDistinctHnames() {
        return ndTypeRepository.findDistinctHnames();
    }

    public List<String> getBnamesByHname(String hname) {
        return ndTypeRepository.findDistinctBnamesByHname(hname);
    }

    public List<NdType> getByHnameAndBname(String hname, String bname) {
        return ndTypeRepository.findByHnameAndBname(hname, bname);
    }

    @Transactional
    public NdType createProduct(NdType ndType) {
        return ndTypeRepository.save(ndType);
    }

    @Transactional
    public NdType updateProduct(Long sn, NdType request) {
        NdType ndType = ndTypeRepository.findById(sn)
                .orElseThrow(() -> new ResourceNotFoundException("商品", sn));
        if (request.getHname() != null) ndType.setHname(request.getHname());
        if (request.getBname() != null) ndType.setBname(request.getBname());
        if (request.getPname() != null) ndType.setPname(request.getPname());
        if (request.getHsort() != null) ndType.setHsort(request.getHsort());
        return ndTypeRepository.save(ndType);
    }

    @Transactional
    public void deleteProduct(Long sn) {
        ndTypeRepository.deleteById(sn);
    }

    // ==================== NdReason ====================
    public List<NdReason> getAllReasons() {
        return ndReasonRepository.findAllByOrderBySnAsc();
    }

    @Transactional
    public NdReason createReason(NdReason reason) {
        reason.setAdate(LocalDateTime.now());
        return ndReasonRepository.save(reason);
    }

    @Transactional
    public NdReason updateReason(Long sn, NdReason request) {
        NdReason reason = ndReasonRepository.findById(sn)
                .orElseThrow(() -> new ResourceNotFoundException("原因", sn));
        if (request.getId() != null) reason.setId(request.getId());
        if (request.getContent() != null) reason.setContent(request.getContent());
        reason.setUdate(LocalDateTime.now());
        return ndReasonRepository.save(reason);
    }

    @Transactional
    public void deleteReason(Long sn) {
        ndReasonRepository.deleteById(sn);
    }
}
