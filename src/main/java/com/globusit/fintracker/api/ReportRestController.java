package com.globusit.fintracker.api;

import com.globusit.fintracker.domain.PeriodType;
import com.globusit.fintracker.domain.User;
import com.globusit.fintracker.dto.ReportForm;
import com.globusit.fintracker.dto.ReportPreview;
import com.globusit.fintracker.service.ExcelReportService;
import com.globusit.fintracker.service.PdfReportService;
import com.globusit.fintracker.service.ReportService;
import com.globusit.fintracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportRestController {

    private final ReportService reportService;
    private final PdfReportService pdfReportService;
    private final ExcelReportService excelReportService;
    private final UserService userService;

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam String format,
                                         @RequestParam PeriodType periodType,
                                         @RequestParam Integer year,
                                         @RequestParam(required = false) Integer month,
                                         @RequestParam(required = false) Integer quarter) {
        User user = userService.getCurrentUser();
        ReportForm form = new ReportForm();
        form.setPeriodType(periodType);
        form.setYear(year);
        form.setMonth(month);
        form.setQuarter(quarter);
        ReportPreview preview = reportService.preview(form, user);
        reportService.saveGeneration(preview, user);

        boolean pdf = "pdf".equalsIgnoreCase(format);
        byte[] body = pdf ? pdfReportService.export(preview) : excelReportService.export(preview);
        String filename = pdf ? "fintracker-report.pdf" : "fintracker-report.xlsx";
        MediaType mediaType = pdf
                ? MediaType.APPLICATION_PDF
                : MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(mediaType)
                .body(body);
    }
}
