package tw.org.nlia.disaster.excel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tw.org.nlia.disaster.common.ApiResponse;
import tw.org.nlia.disaster.config.JwtUserDetails;
import tw.org.nlia.disaster.excel.service.ExcelService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;

    @GetMapping("/export/reports/{ndsn}/{cid}")
    public ResponseEntity<byte[]> exportReports(@PathVariable Long ndsn, @PathVariable String cid)
            throws IOException {
        byte[] data = excelService.exportReportDetails(ndsn, cid);
        String filename = URLEncoder.encode("災損通報明細_" + ndsn + "_" + cid + ".xlsx", StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @PostMapping("/import/reports")
    public ApiResponse<Integer> importReports(@RequestParam Long ndsn,
                                               @RequestParam String cid,
                                               @RequestParam("file") MultipartFile file,
                                               Authentication authentication) throws IOException {
        JwtUserDetails details = (JwtUserDetails) authentication.getDetails();
        int count = excelService.importReportDetails(ndsn, cid, file.getBytes(), details.getUserSn());
        return ApiResponse.success("匯入成功，共 " + count + " 筆", count);
    }
}
