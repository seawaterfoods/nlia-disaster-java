package tw.org.nlia.disaster.excel.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import tw.org.nlia.disaster.entity.NdReportDetail;
import tw.org.nlia.disaster.report.repository.NdReportDetailRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelService {

    private final NdReportDetailRepository reportDetailRepository;

    /**
     * Export report details as Excel (matches PHP rpt_detail_export.php)
     */
    public byte[] exportReportDetails(Long ndsn, String cid) throws IOException {
        List<NdReportDetail> details = reportDetailRepository
                .findByNdsnAndCidAndShowStatusOrderBySnAsc(ndsn, cid, "Y");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("災損通報明細");

            // Header
            String[] headers = {"序號", "證號", "郵遞區號", "縣市", "鄉鎮區",
                    "險種", "大分類", "商品名稱", "出險日期",
                    "預估賠款金額", "預估件數", "預估死亡人數",
                    "已決賠款", "未決賠款", "預付賠款", "結案", "備註"};

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            int rowNum = 1;
            for (NdReportDetail d : details) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rowNum - 1);
                row.createCell(1).setCellValue(d.getBid() != null ? d.getBid() : "");
                row.createCell(2).setCellValue(d.getZip() != null ? d.getZip() : "");
                row.createCell(3).setCellValue(d.getCity() != null ? d.getCity() : "");
                row.createCell(4).setCellValue(d.getArea() != null ? d.getArea() : "");
                row.createCell(5).setCellValue(d.getHname() != null ? d.getHname() : "");
                row.createCell(6).setCellValue(d.getBname() != null ? d.getBname() : "");
                row.createCell(7).setCellValue(d.getPname() != null ? d.getPname() : "");
                row.createCell(8).setCellValue(d.getNdDate() != null ? d.getNdDate().toString() : "");
                if (d.getPreCost() != null) row.createCell(9).setCellValue(d.getPreCost().doubleValue());
                if (d.getPreInum() != null) row.createCell(10).setCellValue(d.getPreInum());
                if (d.getPreDnum() != null) row.createCell(11).setCellValue(d.getPreDnum());
                if (d.getCommited() != null) row.createCell(12).setCellValue(d.getCommited().doubleValue());
                if (d.getPending() != null) row.createCell(13).setCellValue(d.getPending().doubleValue());
                if (d.getPrepay() != null) row.createCell(14).setCellValue(d.getPrepay().doubleValue());
                row.createCell(15).setCellValue(d.getClose() != null ? d.getClose() : "");
                row.createCell(16).setCellValue(d.getMemo() != null ? d.getMemo() : "");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    /**
     * Import report details from Excel (matches PHP rpt_main_nd_import.php)
     */
    public int importReportDetails(Long ndsn, String cid, byte[] fileData, Long adminsn) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(new java.io.ByteArrayInputStream(fileData))) {
            Sheet sheet = workbook.getSheetAt(0);
            int importedCount = 0;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                NdReportDetail detail = NdReportDetail.builder()
                        .ndsn(ndsn)
                        .cid(cid)
                        .bid(getCellString(row, 0))
                        .zip(getCellString(row, 1))
                        .city(getCellString(row, 2))
                        .area(getCellString(row, 3))
                        .hname(getCellString(row, 4))
                        .bname(getCellString(row, 5))
                        .pname(getCellString(row, 6))
                        .showStatus("Y")
                        .adminsn(adminsn)
                        .addSn(adminsn)
                        .adate(java.time.LocalDateTime.now())
                        .build();

                // Parse numeric fields
                try {
                    String preCostStr = getCellString(row, 7);
                    if (!preCostStr.isEmpty()) detail.setPreCost(new java.math.BigDecimal(preCostStr));
                } catch (NumberFormatException ignored) {}

                reportDetailRepository.save(detail);
                importedCount++;
            }
            return importedCount;
        }
    }

    private String getCellString(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}
