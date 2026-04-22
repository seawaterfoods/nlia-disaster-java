package tw.org.nlia.disaster.disaster.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tw.org.nlia.disaster.common.ResourceNotFoundException;
import tw.org.nlia.disaster.disaster.dto.DisasterRequest;
import tw.org.nlia.disaster.disaster.dto.DisasterResponse;
import tw.org.nlia.disaster.disaster.repository.DisasterRepository;
import tw.org.nlia.disaster.entity.Disaster;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisasterServiceTest {

    @Mock private DisasterRepository disasterRepository;

    @InjectMocks
    private DisasterService disasterService;

    private Disaster testDisaster;

    @BeforeEach
    void setUp() {
        testDisaster = Disaster.builder()
                .sn(1L)
                .id("20240115001")
                .title("0115 花蓮地震")
                .content("花蓮外海規模 7.2 地震")
                .ddate(LocalDate.of(2024, 1, 15))
                .adate(LocalDate.of(2024, 1, 15))
                .sdate(LocalDate.of(2024, 1, 15))
                .vdate(LocalDate.of(2024, 2, 15))
                .showStatus("Y")
                .emailNotice("Y")
                .authorCid("88")
                .authorSn(1L)
                .build();
    }

    @Test
    void findAll_returnsAllDisasters() {
        when(disasterRepository.findAllByOrderBySnDesc()).thenReturn(List.of(testDisaster));

        List<DisasterResponse> results = disasterService.findAll();

        assertEquals(1, results.size());
        assertEquals("0115 花蓮地震", results.get(0).getTitle());
        assertEquals("20240115001", results.get(0).getId());
    }

    @Test
    void findActive_returnsOnlyActive() {
        when(disasterRepository.findByShowStatusOrderBySnDesc("Y")).thenReturn(List.of(testDisaster));

        List<DisasterResponse> results = disasterService.findActive();

        assertEquals(1, results.size());
        assertEquals("Y", results.get(0).getShowStatus());
    }

    @Test
    void findById_found() {
        when(disasterRepository.findById(1L)).thenReturn(Optional.of(testDisaster));

        DisasterResponse result = disasterService.findById(1L);

        assertEquals("0115 花蓮地震", result.getTitle());
    }

    @Test
    void findById_notFound_throwsException() {
        when(disasterRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> disasterService.findById(999L));
    }

    @Test
    void create_generatesDisasterId() {
        String todayPrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        when(disasterRepository.findMaxIdByPrefix(todayPrefix)).thenReturn(null);
        when(disasterRepository.save(any(Disaster.class))).thenAnswer(inv -> {
            Disaster d = inv.getArgument(0);
            d.setSn(2L);
            return d;
        });

        DisasterRequest request = DisasterRequest.builder()
                .title("測試災害")
                .ddate(LocalDate.now())
                .build();

        DisasterResponse result = disasterService.create(request, "88", 1L);

        assertEquals(todayPrefix + "001", result.getId());
        assertEquals("測試災害", result.getTitle());
    }

    @Test
    void create_incrementsSequence() {
        String todayPrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        when(disasterRepository.findMaxIdByPrefix(todayPrefix)).thenReturn(todayPrefix + "003");
        when(disasterRepository.save(any(Disaster.class))).thenAnswer(inv -> inv.getArgument(0));

        DisasterRequest request = DisasterRequest.builder().title("第四場").build();
        DisasterResponse result = disasterService.create(request, "88", 1L);

        assertEquals(todayPrefix + "004", result.getId());
    }

    @Test
    void update_partialFields() {
        when(disasterRepository.findById(1L)).thenReturn(Optional.of(testDisaster));
        when(disasterRepository.save(any(Disaster.class))).thenAnswer(inv -> inv.getArgument(0));

        DisasterRequest request = DisasterRequest.builder().title("更新後標題").build();
        DisasterResponse result = disasterService.update(1L, request);

        assertEquals("更新後標題", result.getTitle());
        assertEquals("花蓮外海規模 7.2 地震", result.getContent());
    }

    @Test
    void delete_notFound_throwsException() {
        when(disasterRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> disasterService.delete(999L));
    }
}
