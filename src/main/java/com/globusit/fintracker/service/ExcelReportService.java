package com.globusit.fintracker.service;

import com.globusit.fintracker.domain.OperationType;
import com.globusit.fintracker.domain.Transaction;
import com.globusit.fintracker.dto.ReportPreview;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class ExcelReportService {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public byte[] export(ReportPreview preview) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Отчёт");
            CellStyle header = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            header.setFont(font);
            header.setFillForegroundColor(IndexedColors.TEAL.getIndex());
            header.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row title = sheet.createRow(0);
            title.createCell(0).setCellValue("Финансовый отчёт FinTracker");
            Row period = sheet.createRow(1);
            period.createCell(0).setCellValue("Период: " + DATE.format(preview.getPeriodStart())
                    + " — " + DATE.format(preview.getPeriodEnd()));
            Row totals = sheet.createRow(2);
            totals.createCell(0).setCellValue("Доходы: " + preview.getTotalIncome()
                    + "; Расходы: " + preview.getTotalExpense()
                    + "; Баланс: " + preview.getBalance());

            Row headerRow = sheet.createRow(4);
            String[] headers = {"Дата", "Тип", "Категория", "Описание", "Сумма"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(header);
            }

            int rowIdx = 5;
            for (Transaction tx : preview.getTransactions()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(DATE.format(tx.getDate()));
                row.createCell(1).setCellValue(tx.getType() == OperationType.INCOME ? "Доход" : "Расход");
                row.createCell(2).setCellValue(tx.getCategory().getName());
                row.createCell(3).setCellValue(tx.getDescription() == null ? "" : tx.getDescription());
                row.createCell(4).setCellValue(tx.getAmount().doubleValue());
            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(out);
            log.info("Сформирован Excel-отчёт, транзакций: {}", preview.getTransactions().size());
            return out.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("Не удалось сформировать Excel", ex);
        }
    }
}
