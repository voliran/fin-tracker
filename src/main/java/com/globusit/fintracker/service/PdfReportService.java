package com.globusit.fintracker.service;

import com.globusit.fintracker.domain.OperationType;
import com.globusit.fintracker.domain.Transaction;
import com.globusit.fintracker.dto.ReportPreview;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class PdfReportService {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public byte[] export(ReportPreview preview) {
        try {
            Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = font(16, Font.BOLD);
            Font headerFont = font(10, Font.BOLD);
            Font textFont = font(10, Font.NORMAL);

            document.add(new Paragraph("Финансовый отчёт FinTracker", titleFont));
            document.add(new Paragraph("Период: " + DATE.format(preview.getPeriodStart())
                    + " — " + DATE.format(preview.getPeriodEnd())
                    + " (" + preview.getPeriodType() + ")", textFont));
            document.add(new Paragraph("Доходы: " + preview.getTotalIncome()
                    + "   Расходы: " + preview.getTotalExpense()
                    + "   Баланс: " + preview.getBalance(), textFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(new float[]{2, 2, 3, 4, 3});
            table.setWidthPercentage(100);
            addHeader(table, headerFont, "Дата", "Тип", "Категория", "Описание", "Сумма");
            for (Transaction tx : preview.getTransactions()) {
                addCell(table, textFont, DATE.format(tx.getDate()));
                addCell(table, textFont, tx.getType() == OperationType.INCOME ? "Доход" : "Расход");
                addCell(table, textFont, tx.getCategory().getName());
                addCell(table, textFont, tx.getDescription() == null ? "" : tx.getDescription());
                addCell(table, textFont, tx.getAmount().toPlainString());
            }
            document.add(table);
            document.close();
            log.info("Сформирован PDF-отчёт, транзакций: {}", preview.getTransactions().size());
            return out.toByteArray();
        } catch (DocumentException ex) {
            throw new IllegalStateException("Не удалось сформировать PDF", ex);
        }
    }

    private void addHeader(PdfPTable table, Font font, String... titles) {
        for (String title : titles) {
            PdfPCell cell = new PdfPCell(new Phrase(title, font));
            cell.setBackgroundColor(new BaseColor(15, 118, 110));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            cell.setPhrase(new Phrase(title, new Font(font.getBaseFont(), 10, Font.BOLD, BaseColor.WHITE)));
            table.addCell(cell);
        }
    }

    private void addCell(PdfPTable table, Font font, String value) {
        PdfPCell cell = new PdfPCell(new Phrase(value, font));
        cell.setPadding(5);
        table.addCell(cell);
    }

    private Font font(float size, int style) {
        try {
            BaseFont baseFont = BaseFont.createFont(resolveFontPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            return new Font(baseFont, size, style);
        } catch (Exception ex) {
            log.warn("Не найден шрифт с кириллицей, PDF будет с базовым шрифтом: {}", ex.getMessage());
            return new Font(Font.FontFamily.HELVETICA, size, style);
        }
    }

    private String resolveFontPath() {
        String[] candidates = {
                "C:\\Windows\\Fonts\\arial.ttf",
                "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
                "/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf"
        };
        for (String candidate : candidates) {
            Path path = Paths.get(candidate);
            if (Files.exists(path)) {
                return candidate;
            }
        }
        throw new IllegalStateException("TTF-шрифт не найден");
    }
}
