package com.globusit.fintracker.service;

import com.globusit.fintracker.domain.PeriodType;
import com.globusit.fintracker.dto.ReportForm;
import com.globusit.fintracker.repository.ReportRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ReportServiceTest {

    private final ReportService reportService = new ReportService(mock(TransactionService.class), mock(ReportRepository.class));

    @Test
    void resolveMonthlyRange() {
        ReportForm form = new ReportForm();
        form.setPeriodType(PeriodType.MONTH);
        form.setYear(2026);
        form.setMonth(2);
        LocalDate[] range = reportService.resolveRange(form);
        assertThat(range[0]).isEqualTo(LocalDate.of(2026, 2, 1));
        assertThat(range[1]).isEqualTo(LocalDate.of(2026, 2, 28));
    }

    @Test
    void resolveQuarterRange() {
        ReportForm form = new ReportForm();
        form.setPeriodType(PeriodType.QUARTER);
        form.setYear(2026);
        form.setQuarter(3);
        LocalDate[] range = reportService.resolveRange(form);
        assertThat(range[0]).isEqualTo(LocalDate.of(2026, 7, 1));
        assertThat(range[1]).isEqualTo(LocalDate.of(2026, 9, 30));
    }
}
