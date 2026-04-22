package tw.org.nlia.disaster.syslog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tw.org.nlia.disaster.entity.Syslog;
import tw.org.nlia.disaster.syslog.repository.SyslogRepository;

@Service
@RequiredArgsConstructor
public class SyslogService {

    private final SyslogRepository syslogRepository;

    public Page<Syslog> findFiltered(Long adminsn, String cid, String action, Pageable pageable) {
        return syslogRepository.findFiltered(adminsn, cid, action, pageable);
    }
}
