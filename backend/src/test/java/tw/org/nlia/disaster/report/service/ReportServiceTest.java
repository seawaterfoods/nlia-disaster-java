package tw.org.nlia.disaster.report.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tw.org.nlia.disaster.account.repository.CompanyRepository;
import tw.org.nlia.disaster.alert.service.AlertService;
import tw.org.nlia.disaster.common.ResourceNotFoundException;
import tw.org.nlia.disaster.entity.NdReportMain;
import tw.org.nlia.disaster.report.dto.ReportMainResponse;
import tw.org.nlia.disaster.report.repository.NdReportDetailRepository;
import tw.org.nlia.disaster.report.repository.NdReportMainRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock private NdReportMainRepository reportMainRepository;
    @Mock private NdReportDetailRepository reportDetailRepository;
    @Mock private CompanyRepository companyRepository;
    @Mock private AlertService alertService;

    @InjectMocks
    private ReportService reportService;

    @Test
    void ensureReportMain_existingRecord_returnsExisting() {
        NdReportMain existing = NdReportMain.builder()
                .sn(1L).ndsn(100L).cid("01").nd("X")
                .nd1("X").nd2("X").nd3("X").nd4("X").nd5("X")
                .closs("X").build();

        when(reportMainRepository.findByNdsnAndCid(100L, "01")).thenReturn(Optional.of(existing));

        NdReportMain result = reportService.ensureReportMain(100L, "01", "user@test.com");

        assertEquals(1L, result.getSn());
        verify(reportMainRepository, never()).save(any());
    }

    @Test
    void ensureReportMain_newRecord_createsWithDefaultValues() {
        when(reportMainRepository.findByNdsnAndCid(100L, "01")).thenReturn(Optional.empty());
        when(reportMainRepository.save(any(NdReportMain.class))).thenAnswer(inv -> {
            NdReportMain m = inv.getArgument(0);
            m.setSn(1L);
            return m;
        });

        NdReportMain result = reportService.ensureReportMain(100L, "01", "user@test.com");

        assertEquals("X", result.getNd());
        assertEquals("X", result.getNd1());
        assertEquals("X", result.getNd2());
        assertEquals("X", result.getNd3());
        assertEquals("X", result.getNd4());
        assertEquals("X", result.getNd5());
        assertEquals("X", result.getCloss());
        assertEquals("X", result.getLstatus());
        assertNotNull(result.getAdate());
        assertNotNull(result.getAtime());
    }

    @Test
    void updateMainStatus_setsFieldAndDerivesNd() {
        NdReportMain main = NdReportMain.builder()
                .sn(1L).ndsn(100L).cid("01")
                .nd("X").nd1("X").nd2("X").nd3("X").nd4("X").nd5("X")
                .closs("X").build();

        when(reportMainRepository.findByNdsnAndCid(100L, "01")).thenReturn(Optional.of(main));
        when(reportMainRepository.save(any(NdReportMain.class))).thenAnswer(inv -> inv.getArgument(0));
        when(companyRepository.findById("01")).thenReturn(Optional.empty());

        ReportMainResponse result = reportService.updateMainStatus(100L, "01", "nd1", "Y");

        assertEquals("Y", result.getNd1());
        assertEquals("Y", result.getNd()); // derived: any Y → Y
    }

    @Test
    void updateMainStatus_allN_softDeletesDetails() {
        NdReportMain main = NdReportMain.builder()
                .sn(1L).ndsn(100L).cid("01")
                .nd("X").nd1("N").nd2("N").nd3("N").nd4("N").nd5("X")
                .closs("X").build();

        when(reportMainRepository.findByNdsnAndCid(100L, "01")).thenReturn(Optional.of(main));
        when(reportMainRepository.save(any(NdReportMain.class))).thenAnswer(inv -> inv.getArgument(0));
        when(companyRepository.findById("01")).thenReturn(Optional.empty());

        reportService.updateMainStatus(100L, "01", "nd5", "N");

        // When all nd1~nd5 are N, derived nd = N → soft-delete details
        verify(reportDetailRepository).softDeleteByNdsnAndCid(100L, "01");
    }

    @Test
    void updateMainStatus_notFound_throwsException() {
        when(reportMainRepository.findByNdsnAndCid(999L, "01")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reportService.updateMainStatus(999L, "01", "nd1", "Y"));
    }

    @Test
    void updateMainStatus_mixedYN_derivesY() {
        NdReportMain main = NdReportMain.builder()
                .sn(1L).ndsn(100L).cid("01")
                .nd("X").nd1("Y").nd2("N").nd3("X").nd4("X").nd5("X")
                .closs("X").build();

        when(reportMainRepository.findByNdsnAndCid(100L, "01")).thenReturn(Optional.of(main));
        when(reportMainRepository.save(any(NdReportMain.class))).thenAnswer(inv -> inv.getArgument(0));
        when(companyRepository.findById("01")).thenReturn(Optional.empty());

        ReportMainResponse result = reportService.updateMainStatus(100L, "01", "nd3", "N");

        // nd1=Y, nd2=N, nd3=N → Y takes priority
        assertEquals("Y", result.getNd());
        // Should NOT soft-delete because derived is Y, not N
        verify(reportDetailRepository, never()).softDeleteByNdsnAndCid(anyLong(), anyString());
    }
}
