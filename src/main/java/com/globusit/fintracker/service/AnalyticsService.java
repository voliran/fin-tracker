package com.globusit.fintracker.service;

import com.globusit.fintracker.domain.OperationType;
import com.globusit.fintracker.domain.User;
import com.globusit.fintracker.dto.DashboardMetrics;
import com.globusit.fintracker.repository.TransactionJdbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final TransactionJdbcRepository transactionJdbcRepository;

    public DashboardMetrics getMetrics(User user) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.withDayOfMonth(1);
        LocalDate trendStart = end.minusMonths(5).withDayOfMonth(1);

        BigDecimal income = transactionJdbcRepository.sumByUserTypeAndPeriod(user.getId(), OperationType.INCOME, start, end);
        BigDecimal expense = transactionJdbcRepository.sumByUserTypeAndPeriod(user.getId(), OperationType.EXPENSE, start, end);

        DashboardMetrics metrics = new DashboardMetrics();
        metrics.setTotalIncome(income);
        metrics.setTotalExpense(expense);
        metrics.setBalance(income.subtract(expense));
        metrics.setTransactionCount(transactionJdbcRepository.countByUserAndPeriod(user.getId(), start, end));
        metrics.setIncomeByCategory(transactionJdbcRepository.sumsByCategory(user.getId(), OperationType.INCOME, start, end));
        metrics.setExpenseByCategory(transactionJdbcRepository.sumsByCategory(user.getId(), OperationType.EXPENSE, start, end));
        metrics.setMonthlyTrend(transactionJdbcRepository.monthlyTrend(user.getId(), trendStart, end));
        log.debug("Метрики для userId={}: income={}, expense={}", user.getId(), income, expense);
        return metrics;
    }
}
