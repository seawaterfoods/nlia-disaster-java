package tw.org.nlia.disaster.statistics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tw.org.nlia.disaster.account.repository.CompanyRepository;
import tw.org.nlia.disaster.entity.NdReportDetail;
import tw.org.nlia.disaster.report.repository.NdReportDetailRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final NdReportDetailRepository reportDetailRepository;
    private final CompanyRepository companyRepository;

    /**
     * Statistics by company (matches PHP rpt_view1)
     */
    public List<Map<String, Object>> byCompany(Long ndsn) {
        List<NdReportDetail> details = reportDetailRepository.findAllActiveByNdsn(ndsn);
        Map<String, List<NdReportDetail>> grouped = details.stream()
                .collect(Collectors.groupingBy(NdReportDetail::getCid, LinkedHashMap::new, Collectors.toList()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (var entry : grouped.entrySet()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("cid", entry.getKey());
            row.put("companyName", getCompanyName(entry.getKey()));
            row.put("count", entry.getValue().size());
            row.put("preCostSum", sumField(entry.getValue(), NdReportDetail::getPreCost));
            row.put("commitedSum", sumField(entry.getValue(), NdReportDetail::getCommited));
            row.put("pendingSum", sumField(entry.getValue(), NdReportDetail::getPending));
            row.put("prepaySum", sumField(entry.getValue(), NdReportDetail::getPrepay));
            row.put("preInumSum", entry.getValue().stream().mapToInt(d -> d.getPreInum() != null ? d.getPreInum() : 0).sum());
            row.put("preDnumSum", entry.getValue().stream().mapToInt(d -> d.getPreDnum() != null ? d.getPreDnum() : 0).sum());
            result.add(row);
        }
        return result;
    }

    /**
     * Statistics by area (matches PHP rpt_view2)
     */
    public List<Map<String, Object>> byArea(Long ndsn) {
        List<NdReportDetail> details = reportDetailRepository.findAllActiveByNdsn(ndsn);
        Map<String, List<NdReportDetail>> grouped = details.stream()
                .filter(d -> d.getCity() != null)
                .collect(Collectors.groupingBy(NdReportDetail::getCity, LinkedHashMap::new, Collectors.toList()));

        return buildAggResult(grouped);
    }

    /**
     * Statistics by product type (matches PHP rpt_view3)
     */
    public List<Map<String, Object>> byProduct(Long ndsn) {
        List<NdReportDetail> details = reportDetailRepository.findAllActiveByNdsn(ndsn);
        Map<String, List<NdReportDetail>> grouped = details.stream()
                .filter(d -> d.getHname() != null)
                .collect(Collectors.groupingBy(NdReportDetail::getHname, LinkedHashMap::new, Collectors.toList()));

        return buildAggResult(grouped);
    }

    /**
     * Statistics by area + product (matches PHP rpt_view4)
     */
    public List<Map<String, Object>> byAreaAndProduct(Long ndsn) {
        List<NdReportDetail> details = reportDetailRepository.findAllActiveByNdsn(ndsn);
        Map<String, List<NdReportDetail>> grouped = details.stream()
                .filter(d -> d.getCity() != null && d.getHname() != null)
                .collect(Collectors.groupingBy(
                        d -> d.getCity() + "|" + d.getHname(),
                        LinkedHashMap::new, Collectors.toList()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (var entry : grouped.entrySet()) {
            String[] parts = entry.getKey().split("\\|");
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("city", parts[0]);
            row.put("hname", parts[1]);
            row.put("count", entry.getValue().size());
            row.put("preCostSum", sumField(entry.getValue(), NdReportDetail::getPreCost));
            row.put("commitedSum", sumField(entry.getValue(), NdReportDetail::getCommited));
            row.put("pendingSum", sumField(entry.getValue(), NdReportDetail::getPending));
            row.put("prepaySum", sumField(entry.getValue(), NdReportDetail::getPrepay));
            result.add(row);
        }
        return result;
    }

    private List<Map<String, Object>> buildAggResult(Map<String, List<NdReportDetail>> grouped) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (var entry : grouped.entrySet()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("name", entry.getKey());
            row.put("count", entry.getValue().size());
            row.put("preCostSum", sumField(entry.getValue(), NdReportDetail::getPreCost));
            row.put("commitedSum", sumField(entry.getValue(), NdReportDetail::getCommited));
            row.put("pendingSum", sumField(entry.getValue(), NdReportDetail::getPending));
            row.put("prepaySum", sumField(entry.getValue(), NdReportDetail::getPrepay));
            row.put("preInumSum", entry.getValue().stream().mapToInt(d -> d.getPreInum() != null ? d.getPreInum() : 0).sum());
            row.put("preDnumSum", entry.getValue().stream().mapToInt(d -> d.getPreDnum() != null ? d.getPreDnum() : 0).sum());
            result.add(row);
        }
        return result;
    }

    private BigDecimal sumField(List<NdReportDetail> list, java.util.function.Function<NdReportDetail, BigDecimal> getter) {
        return list.stream()
                .map(getter)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String getCompanyName(String cid) {
        return companyRepository.findById(cid)
                .map(c -> c.getCname())
                .orElse(cid);
    }
}
