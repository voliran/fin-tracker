package com.globusit.fintracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMetrics {

    private BigDecimal totalIncome = BigDecimal.ZERO;
    private BigDecimal totalExpense = BigDecimal.ZERO;
    private BigDecimal balance = BigDecimal.ZERO;
    private long transactionCount;
    private List<NamedAmount> incomeByCategory = new ArrayList<>();
    private List<NamedAmount> expenseByCategory = new ArrayList<>();
    private List<MonthlyPoint> monthlyTrend = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NamedAmount {
        private String name;
        private BigDecimal value;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyPoint {
        private String label;
        private BigDecimal income;
        private BigDecimal expense;
    }
}
