package com.globusit.fintracker.service;

import com.globusit.fintracker.domain.OperationType;
import com.globusit.fintracker.domain.PeriodType;
import com.globusit.fintracker.domain.Report;
import com.globusit.fintracker.domain.Transaction;
import com.globusit.fintracker.domain.User;
import com.globusit.fintracker.dto.ReportForm;
import com.globusit.fintracker.dto.ReportPreview;
import com.globusit.fintracker.exception.BusinessException;
import com.globusit.fintracker.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionService transactionService;
    private final ReportRepository reportRepository;

    public ReportPreview preview(ReportForm form, User user) {
        LocalDate[] range = resolveRange(form);
        List<Transaction> transactions = transactionService.findForPeriod(user, range[0], range[1]);
        BigDecimal income = sum(transactions, OperationType.INCOME);
        BigDecimal expense = sum(transactions, OperationType.EXPENSE);
        return ReportPreview.builder()
                .periodType(form.getPeriodType())
                .periodStart(range[0])
                .periodEnd(range[1])
                .totalIncome(income)
                .totalExpense(expense)
                .balance(income.subtract(expense))
                .transactions(transactions)
                .build();
    }

    @Transactional
    public Report saveGeneration(ReportPreview preview, User user) {
        Report report = new Report();
        report.setPeriodType(preview.getPeriodType());
        report.setPeriodStart(preview.getPeriodStart());
        report.setPeriodEnd(preview.getPeriodEnd());
        report.setGeneratedAt(LocalDateTime.now());
        report.setUser(user);
        Report saved = reportRepository.save(report);
        log.info("Сгенерирован отчёт id={}, userId={}, period={}..{}",
                saved.getId(), user.getId(), saved.getPeriodStart(), saved.getPeriodEnd());
        return saved;
    }

    public List<Report> history(User user) {
        return reportRepository.findByUserOrderByGeneratedAtDesc(user);
    }

    public LocalDate[] resolveRange(ReportForm form) {
        int year = form.getYear();
        if (form.getPeriodType() == PeriodType.YEAR) {
            return new LocalDate[]{LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31)};
        }
        if (form.getPeriodType() == PeriodType.QUARTER) {
            Integer quarter = form.getQuarter();
            if (quarter == null) {
                throw new BusinessException("Укажите квартал");
            }
            Month firstMonth = Month.of((quarter - 1) * 3 + 1);
            LocalDate start = LocalDate.of(year, firstMonth, 1);
            LocalDate end = start.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
            return new LocalDate[]{start, end};
        }
        Integer month = form.getMonth();
        if (month == null) {
            throw new BusinessException("Укажите месяц");
        }
        LocalDate start = LocalDate.of(year, month, 1);
        return new LocalDate[]{start, start.with(TemporalAdjusters.lastDayOfMonth())};
    }

    public ReportForm currentPeriodForm() {
        LocalDate now = LocalDate.now();
        ReportForm form = new ReportForm();
        form.setPeriodType(PeriodType.MONTH);
        form.setYear(now.getYear());
        form.setMonth(now.getMonthValue());
        form.setQuarter(now.get(IsoFields.QUARTER_OF_YEAR));
        return form;
    }

    private BigDecimal sum(List<Transaction> transactions, OperationType type) {
        return transactions.stream()
                .filter(tx -> tx.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
